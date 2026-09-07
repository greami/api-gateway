import { useEffect, useMemo, useState } from 'react';
import { ProtectedLayout } from '../components/layout/ProtectedLayout';
import { EmptyState } from '../components/status/EmptyState';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { strings } from '../config/strings';
import { getBookingsByUserId, getEvents } from '../lib/museumApi';
import { isFutureDate } from '../lib/date';
import type { Evento, Prenotazione } from '../lib/types';
import { EventCard } from '../components/event/EventCard';
import { useAuth } from '../state/AuthContext';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';

export function HomePage() {
  const { user } = useAuth();
  const [bookings, setBookings] = useState<Prenotazione[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [fatalError, setFatalError] = useState<Error | null>(null);

  useEffect(() => {
    if (!user) return;
    setLoading(true);
    Promise.all([getBookingsByUserId(user.idUtente), getEvents()])
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
  }, [user]);

  if (fatalError) {
    return <GenericServerErrorPage error={fatalError} />;
  }

  const futureBookings = useMemo(
    () =>
      bookings
        .map((booking) => ({
          booking,
          event: events.find((item) => item.idEvento === booking.idEvento),
        }))
        .filter((item): item is { booking: Prenotazione; event: Evento } => Boolean(item.event))
        .filter((item) => isFutureDate(item.event.data)),
    [bookings, events],
  );

  return (
    <ProtectedLayout>
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-semibold text-white">{strings.home.title}</h1>
        </div>

        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}
        {!loading && futureBookings.length === 0 ? <EmptyState title={strings.home.emptyState} /> : null}

        <div className="grid gap-4">
          {futureBookings.map(({ booking, event }) => (
            <EventCard
              key={booking.idPrenotazione}
              evento={event}
              booked
              hasPastReservation={false}
              bookingDescription={`${strings.home.bookingPrefix} #${booking.idPrenotazione} · ${booking.numeroPartecipanti} ${strings.home.participantsSuffix}`}
            />
          ))}
        </div>
      </div>
    </ProtectedLayout>
  );
}
