import { useEffect, useState } from 'react';
import { ProtectedLayout } from '../components/layout/ProtectedLayout';
import { EmptyState } from '../components/status/EmptyState';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { strings } from '../config/strings';
import { getBookingsByUsername, getEvents, createBooking, updateBooking, deleteBooking } from '../lib/museumApi';
import { isFutureDate } from '../lib/date';
import type { Evento, Prenotazione } from '../lib/types';
import { useAuth } from '../state/AuthContext';
import { appConfig } from '../config/appConfig';
import { EventCard } from '../components/event/EventCard';
import { BookingModal } from '../components/forms/BookingModal';
import { DeleteConfirmModal } from '../components/forms/DeleteConfirmModal';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';
import { NotFoundPage } from './NotFoundPage';

type ModalState =
  | { type: 'create'; evento: Evento }
  | { type: 'edit'; evento: Evento; booking: Prenotazione }
  | { type: 'delete'; booking: Prenotazione }
  | null;

export function BookingsPage() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState<Prenotazione[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [fatalError, setFatalError] = useState<Error | null>(null);
  const [modal, setModal] = useState<ModalState>(null);
  const [busy, setBusy] = useState(false);
  const featureEnabled = appConfig.featureFlags.bookings;
  const futureEvents = events.filter((event) => isFutureDate(event.data));

  useEffect(() => {
    if (!featureEnabled) return;
    if (!user) return;
    setLoading(true);
    Promise.all([getBookingsByUsername(user.nomeUtente), getEvents()])
      .then(([userBookings, allEvents]) => {
        setError(null);
        setBookings(userBookings);
        setEvents(allEvents);
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
          <h1 className="text-3xl font-semibold text-white">{strings.bookings.title}</h1>
        </div>

        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}
        {!loading && futureEvents.length === 0 ? <EmptyState title={strings.bookings.emptyState} /> : null}

        <div className="grid gap-4 md:grid-cols-2">
          {futureEvents.map((event) => {
            const booking = bookings.find((item) => item.idEvento === event.idEvento) ?? null;
            return (
              <EventCard
                key={event.idEvento}
                evento={event}
                booked={Boolean(booking)}
                hasPastReservation={false}
                onBook={booking ? undefined : () => setModal({ type: 'create', evento: event })}
                onEdit={booking ? () => setModal({ type: 'edit', evento: event, booking }) : undefined}
                onDelete={booking ? () => setModal({ type: 'delete', booking }) : undefined}
              />
            );
          })}
        </div>

        {modal?.type === 'create' ? (
          <BookingModal
            title={`${strings.bookings.book}: ${modal.evento.nome}`}
            confirmLabel={strings.modal.confirm}
            onClose={() => setModal(null)}
            onConfirm={async (participants) => {
              if (!user) return;
              setBusy(true);
              try {
                await createBooking({
                  idUtente: user.idUtente,
                  idEvento: modal.evento.idEvento,
                  numeroPartecipanti: participants,
                });
                await Promise.all([getBookingsByUsername(user.nomeUtente), getEvents()]).then(([userBookings, allEvents]) => {
                  setError(null);
                  setBookings(userBookings);
                  setEvents(allEvents);
                });
                setModal(null);
              } catch (caughtError) {
                handleActionError(caughtError);
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}

        {modal?.type === 'edit' ? (
          <BookingModal
            title={`${strings.bookings.edit}: ${modal.evento.nome}`}
            confirmLabel={strings.modal.confirm}
            initialParticipants={modal.booking.numeroPartecipanti}
            onClose={() => setModal(null)}
            onConfirm={async (participants) => {
              setBusy(true);
              try {
                await updateBooking(modal.booking.idPrenotazione, { numeroPartecipanti: participants });
                const [userBookings, allEvents] = await Promise.all([getBookingsByUsername(user!.nomeUtente), getEvents()]);
                setError(null);
                setBookings(userBookings);
                setEvents(allEvents);
                setModal(null);
              } catch (caughtError) {
                handleActionError(caughtError);
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}

        {modal?.type === 'delete' ? (
          <DeleteConfirmModal
            title={strings.bookings.delete}
            onClose={() => setModal(null)}
            onConfirm={async () => {
              setBusy(true);
              try {
                await deleteBooking(modal.booking.idPrenotazione);
                const [userBookings, allEvents] = await Promise.all([getBookingsByUsername(user!.nomeUtente), getEvents()]);
                setError(null);
                setBookings(userBookings);
                setEvents(allEvents);
                setModal(null);
              } catch (caughtError) {
                handleActionError(caughtError);
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}

        {busy ? <LoadingState title={strings.appName} /> : null}
      </div>
    </ProtectedLayout>
  );
}
