import { useEffect, useState } from 'react';
import { ProtectedLayout } from '../components/layout/ProtectedLayout';
import { EmptyState } from '../components/status/EmptyState';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { strings } from '../config/strings';
import {
  createFeedbackEvent,
  deleteFeedbackEvent,
  getBookingsByUserId,
  getEvents,
  getFeedbacksByUserId,
  updateFeedbackEvent,
} from '../lib/museumApi';
import { isFutureDate } from '../lib/date';
import type { Evento, FeedbackEvento, Prenotazione } from '../lib/types';
import { useAuth } from '../state/AuthContext';
import { appConfig } from '../config/appConfig';
import { EventCard } from '../components/event/EventCard';
import { DeleteConfirmModal } from '../components/forms/DeleteConfirmModal';
import { FeedbackModal } from '../components/forms/FeedbackModal';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';
import { NotFoundPage } from './NotFoundPage';
import { ResultModal } from '../components/forms/ResultModal';

type FeedbackDraft = { voto: number; note: string };

type FeedbackFormModalState =
  | {
      mode: 'create' | 'edit';
      evento: Evento;
      feedback?: FeedbackEvento;
      draft: FeedbackDraft;
    }
  | null;

type DeleteModalState =
  | {
      evento: Evento;
      feedback: FeedbackEvento;
    }
  | null;

type ResultModalState =
  | {
      title: string;
      message: string;
      onOk: () => void;
    }
  | null;

export function FeedbackPage() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState<Prenotazione[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [feedbacks, setFeedbacks] = useState<FeedbackEvento[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [fatalError, setFatalError] = useState<Error | null>(null);
  const [formModal, setFormModal] = useState<FeedbackFormModalState>(null);
  const [deleteModal, setDeleteModal] = useState<DeleteModalState>(null);
  const [resultModal, setResultModal] = useState<ResultModalState>(null);
  const [busy, setBusy] = useState(false);
  const featureEnabled = appConfig.featureFlags.feedback;
  const pastEvents = bookings
    .map((booking) => events.find((event) => event.idEvento === booking.idEvento))
    .filter((event): event is Evento => Boolean(event))
    .filter((event) => !isFutureDate(event.data));

  const refreshFeedbackData = async (userId: number) => {
    const [userBookings, allEvents, userFeedbacks] = await Promise.all([
      getBookingsByUserId(userId),
      getEvents(),
      getFeedbacksByUserId(userId),
    ]);
    setError(null);
    setBookings(userBookings);
    setEvents(allEvents);
    setFeedbacks(userFeedbacks);
  };

  useEffect(() => {
    if (!featureEnabled) return;
    if (!user) return;
    setLoading(true);
    refreshFeedbackData(user.idUtente)
      .catch((caughtError: unknown) => {
        if (caughtError instanceof ApiError && caughtError.status >= 500) {
          setFatalError(caughtError);
          return;
        }
        setError(caughtError instanceof Error ? caughtError.message : strings.errors.unknown);
      })
      .finally(() => setLoading(false));
  }, [featureEnabled, user]);

  if (fatalError) {
    return <GenericServerErrorPage error={fatalError} />;
  }

  if (!featureEnabled) {
    return <NotFoundPage />;
  }

  const buildErrorMessage = (caughtError: unknown) =>
    caughtError instanceof Error ? caughtError.message : strings.errors.unknown;

  const closeResultModal = () => setResultModal(null);

  const openCreateModal = (evento: Evento) => {
    setError(null);
    setDeleteModal(null);
    setFormModal({
      mode: 'create',
      evento,
      draft: { voto: 5, note: '' },
    });
  };

  const openEditModal = (evento: Evento, feedback: FeedbackEvento) => {
    setError(null);
    setDeleteModal(null);
    setFormModal({
      mode: 'edit',
      evento,
      feedback,
      draft: { voto: feedback.voto, note: feedback.note },
    });
  };

  const openDeleteModal = (evento: Evento, feedback: FeedbackEvento) => {
    setError(null);
    setFormModal(null);
    setDeleteModal({ evento, feedback });
  };

  return (
    <ProtectedLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-semibold text-white">{strings.feedback.title}</h1>
        </div>

        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}
        {!loading && pastEvents.length === 0 ? <EmptyState title={strings.feedback.emptyState} /> : null}

        <div className="grid gap-4 md:grid-cols-2">
          {pastEvents.map((event) => {
            const feedback = feedbacks.find((item) => item.idEvento === event.idEvento);
            return (
              <EventCard
                key={event.idEvento}
                evento={event}
                booked
                hasPastReservation
                bookingDescription={feedback ? strings.feedback.alreadyPresent : strings.feedback.available}
                feedbackActionLabel={feedback ? strings.feedback.edit : strings.feedback.submit}
                onFeedback={() => (feedback ? openEditModal(event, feedback) : openCreateModal(event))}
                onFeedbackDelete={feedback ? () => openDeleteModal(event, feedback) : undefined}
              />
            );
          })}
        </div>

        {formModal ? (
          <FeedbackModal
            title={`${formModal.mode === 'create' ? strings.feedback.submit : strings.feedback.edit}: ${formModal.evento.nome}`}
            confirmLabel={strings.modal.confirm}
            initialVoto={formModal.draft.voto}
            initialNote={formModal.draft.note}
            busy={busy}
            onClose={() => setFormModal(null)}
            onConfirm={async (payload) => {
              if (!user) return;
              const currentModal = {
                ...formModal,
                draft: payload,
              };
              setBusy(true);
              try {
                if (formModal.mode === 'create') {
                  await createFeedbackEvent({
                    idUtente: user.idUtente,
                    idEvento: formModal.evento.idEvento,
                    voto: payload.voto,
                    note: payload.note,
                  });
                } else {
                  await updateFeedbackEvent(formModal.feedback!.idFeedback, {
                    voto: payload.voto,
                    note: payload.note,
                  });
                }
                await refreshFeedbackData(user.idUtente);
                setFormModal(null);
                setResultModal({
                  title:
                    formModal.mode === 'create'
                      ? strings.feedback.createSuccessTitle
                      : strings.feedback.updateSuccessTitle,
                  message:
                    formModal.mode === 'create'
                      ? strings.feedback.createSuccessMessage
                      : strings.feedback.updateSuccessMessage,
                  onOk: closeResultModal,
                });
              } catch (caughtError) {
                setFormModal(null);
                setResultModal({
                  title: strings.feedback.errorTitle,
                  message: buildErrorMessage(caughtError),
                  onOk: () => {
                    closeResultModal();
                    setFormModal(currentModal);
                  },
                });
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}

        {deleteModal ? (
          <DeleteConfirmModal
            title={strings.feedback.delete}
            message={strings.modal.feedbackDeleteQuestion}
            confirmLabel={strings.modal.ok}
            confirmVariant="danger"
            busy={busy}
            onClose={() => setDeleteModal(null)}
            onConfirm={async () => {
              if (!user) return;
              const currentModal = deleteModal;
              setBusy(true);
              try {
                await deleteFeedbackEvent(deleteModal.feedback.idFeedback);
                await refreshFeedbackData(user.idUtente);
                setDeleteModal(null);
                setResultModal({
                  title: strings.feedback.deleteSuccessTitle,
                  message: strings.feedback.deleteSuccessMessage,
                  onOk: closeResultModal,
                });
              } catch (caughtError) {
                setDeleteModal(null);
                setResultModal({
                  title: strings.feedback.errorTitle,
                  message: buildErrorMessage(caughtError),
                  onOk: () => {
                    closeResultModal();
                    setDeleteModal(currentModal);
                  },
                });
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}

        {resultModal ? (
          <ResultModal title={resultModal.title} message={resultModal.message} onOk={resultModal.onOk} />
        ) : null}

        {busy ? <LoadingState title={strings.appName} /> : null}
      </div>
    </ProtectedLayout>
  );
}
