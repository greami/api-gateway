import { useEffect, useMemo, useState } from 'react';
import { ProtectedLayout } from '../components/layout/ProtectedLayout';
import { EmptyState } from '../components/status/EmptyState';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { strings } from '../config/strings';
import {
  createFeedbackEvent,
  createFeedbackOpera,
  deleteFeedbackEvent,
  deleteFeedbackOpera,
  getBookingsByUserId,
  getEvents,
  getFeedbacksByUserId,
  getFeedbacksOpereByUserId,
  getOpere,
  getOpereByEvento,
  updateFeedbackEvent,
  updateFeedbackOpera,
} from '../lib/museumApi';
import { isFutureDate } from '../lib/date';
import type {
  Evento,
  EventoOpera,
  FeedbackEvento,
  FeedbackOpera,
  Opera,
  Prenotazione,
} from '../lib/types';
import { useAuth } from '../state/AuthContext';
import { appConfig } from '../config/appConfig';
import { EventCard } from '../components/event/EventCard';
import { OperaCard } from '../components/opera/OperaCard';
import { DeleteConfirmModal } from '../components/forms/DeleteConfirmModal';
import { FeedbackModal } from '../components/forms/FeedbackModal';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';
import { NotFoundPage } from './NotFoundPage';
import { ResultModal } from '../components/forms/ResultModal';

type FeedbackDraft = { voto: number; note: string };
type FeedbackResource = { kind: 'evento'; item: Evento } | { kind: 'opera'; item: Opera };

type FeedbackFormModalState =
  | {
      resource: FeedbackResource;
      mode: 'create' | 'edit';
      feedback?: FeedbackEvento | FeedbackOpera;
      draft: FeedbackDraft;
    }
  | null;

type DeleteModalState =
  | {
      resource: FeedbackResource;
      feedback: FeedbackEvento | FeedbackOpera;
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
  const [activeTab, setActiveTab] = useState<'events' | 'works'>('events');
  const [bookings, setBookings] = useState<Prenotazione[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [eventFeedbacks, setEventFeedbacks] = useState<FeedbackEvento[]>([]);
  const [works, setWorks] = useState<Opera[]>([]);
  const [eventWorks, setEventWorks] = useState<EventoOpera[]>([]);
  const [workFeedbacks, setWorkFeedbacks] = useState<FeedbackOpera[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [fatalError, setFatalError] = useState<Error | null>(null);
  const [formModal, setFormModal] = useState<FeedbackFormModalState>(null);
  const [deleteModal, setDeleteModal] = useState<DeleteModalState>(null);
  const [resultModal, setResultModal] = useState<ResultModalState>(null);
  const [busy, setBusy] = useState(false);
  const featureEnabled = appConfig.featureFlags.feedback;

  const pastEvents = useMemo(
    () =>
      bookings
        .map((booking) => events.find((event) => event.idEvento === booking.idEvento))
        .filter((event): event is Evento => Boolean(event))
        .filter((event) => !isFutureDate(event.data)),
    [bookings, events],
  );

  const pastEventIds = useMemo(() => new Set(pastEvents.map((event) => event.idEvento)), [pastEvents]);

  const pastWorks = useMemo(() => {
    const associatedWorkIds = new Set(
      eventWorks
        .filter((association) => pastEventIds.has(association.idEvento))
        .map((association) => association.idOpera),
    );
    return works.filter((work) => associatedWorkIds.has(work.idOpera));
  }, [eventWorks, pastEventIds, works]);

  const refreshFeedbackData = async (userId: number) => {
    const [userBookings, allEvents, userEventFeedbacks, allWorks, userWorkFeedbacks] = await Promise.all([
      getBookingsByUserId(userId),
      getEvents(),
      getFeedbacksByUserId(userId),
      getOpere(),
      getFeedbacksOpereByUserId(userId),
    ]);
    const pastEventIdsForRefresh = new Set(
      userBookings
        .map((booking) => allEvents.find((event) => event.idEvento === booking.idEvento))
        .filter((event): event is Evento => Boolean(event))
        .filter((event) => !isFutureDate(event.data))
        .map((event) => event.idEvento),
    );
    const associations = (
      await Promise.all([...pastEventIdsForRefresh].map((idEvento) => getOpereByEvento(idEvento)))
    ).flat();

    setError(null);
    setBookings(userBookings);
    setEvents(allEvents);
    setEventFeedbacks(userEventFeedbacks);
    setWorks(allWorks);
    setEventWorks(associations);
    setWorkFeedbacks(userWorkFeedbacks);
  };

  useEffect(() => {
    if (!featureEnabled || !user) return;
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

  if (fatalError) return <GenericServerErrorPage error={fatalError} />;
  if (!featureEnabled) return <NotFoundPage />;

  const buildErrorMessage = (caughtError: unknown) =>
    caughtError instanceof Error ? caughtError.message : strings.errors.unknown;

  const closeResultModal = () => setResultModal(null);

  const openCreateModal = (resource: FeedbackResource) => {
    setError(null);
    setDeleteModal(null);
    setFormModal({ resource, mode: 'create', draft: { voto: 5, note: '' } });
  };

  const openEditModal = (resource: FeedbackResource, feedback: FeedbackEvento | FeedbackOpera) => {
    setError(null);
    setDeleteModal(null);
    setFormModal({
      resource,
      mode: 'edit',
      feedback,
      draft: { voto: feedback.voto, note: feedback.note },
    });
  };

  const openDeleteModal = (resource: FeedbackResource, feedback: FeedbackEvento | FeedbackOpera) => {
    setError(null);
    setFormModal(null);
    setDeleteModal({ resource, feedback });
  };

  const eventResource = (event: Evento): FeedbackResource => ({ kind: 'evento', item: event });
  const workResource = (work: Opera): FeedbackResource => ({ kind: 'opera', item: work });

  return (
    <ProtectedLayout>
      <div className="space-y-6">
        <h1 className="text-3xl font-semibold text-white">{strings.feedback.title}</h1>

        <div className="flex gap-2 border-b border-white/10">
          <button
            type="button"
            onClick={() => setActiveTab('events')}
            className={`border-b-2 px-4 py-3 text-sm ${activeTab === 'events' ? 'border-cyan-400 text-cyan-300' : 'border-transparent text-slate-400'}`}
          >
            {strings.feedback.eventsTab}
          </button>
          <button
            type="button"
            onClick={() => setActiveTab('works')}
            className={`border-b-2 px-4 py-3 text-sm ${activeTab === 'works' ? 'border-cyan-400 text-cyan-300' : 'border-transparent text-slate-400'}`}
          >
            {strings.feedback.worksTab}
          </button>
        </div>

        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}

        {activeTab === 'events' ? (
          !loading && pastEvents.length === 0 ? (
            <EmptyState title={strings.feedback.emptyState} />
          ) : (
            <div className="grid gap-4 md:grid-cols-2">
              {pastEvents.map((event) => {
                const feedback = eventFeedbacks.find((item) => item.idEvento === event.idEvento);
                const resource = eventResource(event);
                return (
                  <EventCard
                    key={event.idEvento}
                    evento={event}
                    booked
                    hasPastReservation
                    bookingDescription={feedback ? strings.feedback.alreadyPresent : strings.feedback.available}
                    feedbackActionLabel={feedback ? strings.feedback.edit : strings.feedback.submit}
                    onFeedback={() => (feedback ? openEditModal(resource, feedback) : openCreateModal(resource))}
                    onFeedbackDelete={feedback ? () => openDeleteModal(resource, feedback) : undefined}
                  />
                );
              })}
            </div>
          )
        ) : !loading && pastWorks.length === 0 ? (
          <EmptyState title={strings.feedback.worksEmptyState} />
        ) : (
          <div className="grid gap-4 md:grid-cols-2">
            {pastWorks.map((work) => {
              const feedback = workFeedbacks.find((item) => item.idOpera === work.idOpera);
              const resource = workResource(work);
              return (
                <OperaCard
                  key={work.idOpera}
                  opera={work}
                  hasFeedback={Boolean(feedback)}
                  onFeedback={() => (feedback ? openEditModal(resource, feedback) : openCreateModal(resource))}
                  onFeedbackDelete={feedback ? () => openDeleteModal(resource, feedback) : undefined}
                />
              );
            })}
          </div>
        )}

        {formModal ? (
          <FeedbackModal
            title={`${formModal.mode === 'create' ? strings.feedback.submit : strings.feedback.edit}: ${formModal.resource.item.nome}`}
            confirmLabel={strings.modal.confirm}
            initialVoto={formModal.draft.voto}
            initialNote={formModal.draft.note}
            busy={busy}
            onClose={() => setFormModal(null)}
            onConfirm={async (payload) => {
              if (!user) return;
              const currentModal = { ...formModal, draft: payload };
              setBusy(true);
              try {
                if (formModal.resource.kind === 'evento') {
                  if (formModal.mode === 'create') {
                    await createFeedbackEvent({
                      idUtente: user.idUtente,
                      idEvento: formModal.resource.item.idEvento,
                      voto: payload.voto,
                      note: payload.note,
                    });
                  } else {
                    await updateFeedbackEvent((formModal.feedback as FeedbackEvento).idFeedback, payload);
                  }
                } else if (formModal.mode === 'create') {
                  await createFeedbackOpera({
                    idUtente: user.idUtente,
                    idOpera: formModal.resource.item.idOpera,
                    voto: payload.voto,
                    note: payload.note,
                  });
                } else {
                  await updateFeedbackOpera((formModal.feedback as FeedbackOpera).idFeedback, payload);
                }
                await refreshFeedbackData(user.idUtente);
                setFormModal(null);
                setResultModal({
                  title: formModal.mode === 'create' ? strings.feedback.createSuccessTitle : strings.feedback.updateSuccessTitle,
                  message: formModal.mode === 'create' ? strings.feedback.createSuccessMessage : strings.feedback.updateSuccessMessage,
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
                if (deleteModal.resource.kind === 'evento') {
                  await deleteFeedbackEvent((deleteModal.feedback as FeedbackEvento).idFeedback);
                } else {
                  await deleteFeedbackOpera((deleteModal.feedback as FeedbackOpera).idFeedback);
                }
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

        {resultModal ? <ResultModal title={resultModal.title} message={resultModal.message} onOk={resultModal.onOk} /> : null}
        {busy ? <LoadingState title={strings.appName} /> : null}
      </div>
    </ProtectedLayout>
  );
}
