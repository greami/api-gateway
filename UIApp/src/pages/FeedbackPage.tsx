import { useEffect, useState } from 'react';
import { ProtectedLayout } from '../components/layout/ProtectedLayout';
import { EmptyState } from '../components/status/EmptyState';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { strings } from '../config/strings';
import { getBookingsByUserId, getEvents, createFeedbackEvent, getFeedbacksByUserId } from '../lib/museumApi';
import { isFutureDate } from '../lib/date';
import type { Evento, FeedbackEvento, Prenotazione } from '../lib/types';
import { useAuth } from '../state/AuthContext';
import { appConfig } from '../config/appConfig';
import { EventCard } from '../components/event/EventCard';
import { FeedbackModal } from '../components/forms/FeedbackModal';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';
import { NotFoundPage } from './NotFoundPage';

type ModalState = { evento: Evento } | null;

export function FeedbackPage() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState<Prenotazione[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [feedbacks, setFeedbacks] = useState<FeedbackEvento[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [fatalError, setFatalError] = useState<Error | null>(null);
  const [modal, setModal] = useState<ModalState>(null);
  const featureEnabled = appConfig.featureFlags.feedback;
  const pastEvents = bookings
    .map((booking) => events.find((event) => event.idEvento === booking.idEvento))
    .filter((event): event is Evento => Boolean(event))
    .filter((event) => !isFutureDate(event.data));

  useEffect(() => {
    if (!featureEnabled) return;
    if (!user) return;
    setLoading(true);
    Promise.all([getBookingsByUserId(user.idUtente), getEvents(), getFeedbacksByUserId(user.idUtente)])
      .then(([userBookings, allEvents, userFeedbacks]) => {
        setError(null);
        setBookings(userBookings);
        setEvents(allEvents);
        setFeedbacks(userFeedbacks);
      })
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

  const handleActionError = (caughtError: unknown) => {
    if (caughtError instanceof ApiError && caughtError.status >= 500) {
      setFatalError(caughtError);
      return;
    }
    setError(caughtError instanceof Error ? caughtError.message : strings.errors.unknown);
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
            const hasFeedback = feedbacks.some((feedback) => feedback.idEvento === event.idEvento);
            return (
              <EventCard
                key={event.idEvento}
                evento={event}
                booked
                hasPastReservation
                bookingDescription={hasFeedback ? strings.feedback.alreadyPresent : strings.feedback.available}
                onFeedback={() => setModal({ evento: event })}
              />
            );
          })}
        </div>

        {modal ? (
          <FeedbackModal
            title={`${strings.feedback.submit}: ${modal.evento.nome}`}
            onClose={() => setModal(null)}
            onConfirm={async (payload) => {
              if (!user) return;
              try {
                await createFeedbackEvent({
                  idUtente: user.idUtente,
                  idEvento: modal.evento.idEvento,
                  voto: payload.voto,
                  note: payload.note,
                });
                const [userBookings, allEvents, userFeedbacks] = await Promise.all([
                  getBookingsByUserId(user.idUtente),
                  getEvents(),
                  getFeedbacksByUserId(user.idUtente),
                ]);
                setError(null);
                setBookings(userBookings);
                setEvents(allEvents);
                setFeedbacks(userFeedbacks);
                setModal(null);
              } catch (caughtError) {
                handleActionError(caughtError);
              }
            }}
          />
        ) : null}
      </div>
    </ProtectedLayout>
  );
}
