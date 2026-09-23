import { useEffect, useMemo, useState } from 'react';
import { ProtectedLayout } from '../components/layout/ProtectedLayout';
import { EmptyState } from '../components/status/EmptyState';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { strings } from '../config/strings';
import {
  createBooking,
  deleteBooking,
  getBookingsByUserId,
  getEvents,
  getEventsByDate,
  getEventsByName,
  updateBooking,
} from '../lib/museumApi';
import { isFutureDate } from '../lib/date';
import type { Evento, Prenotazione } from '../lib/types';
import { useAuth } from '../state/AuthContext';
import { appConfig } from '../config/appConfig';
import { EventCard } from '../components/event/EventCard';
import { BookingModal } from '../components/forms/BookingModal';
import { DeleteConfirmModal } from '../components/forms/DeleteConfirmModal';
import { ResultModal } from '../components/forms/ResultModal';
import { Button } from '../components/ui/Button';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';
import { AdminBookingsPage } from './admin/AdminBookingsPage';
import { NotFoundPage } from './NotFoundPage';

type Tab = 'events' | 'bookings';
type SortMode = 'dateAsc' | 'dateDesc' | 'nameAsc' | 'nameDesc';
type ModalState =
  | { type: 'create'; evento: Evento }
  | { type: 'edit'; evento: Evento; booking: Prenotazione }
  | { type: 'delete'; booking: Prenotazione; evento: Evento }
  | null;
type ResultState = { success: boolean; message: string } | null;

function toInputDate(date: Date) {
  return `${String(date.getDate()).padStart(2, '0')}-${String(date.getMonth() + 1).padStart(2, '0')}-${date.getFullYear()}`;
}

function toIsoDate(value: string, endOfDay = false) {
  const match = /^(\d{2})-(\d{2})-(\d{4})$/.exec(value);
  if (!match) return null;
  const date = new Date(
    Number(match[3]),
    Number(match[2]) - 1,
    Number(match[1]),
    endOfDay ? 23 : 0,
    endOfDay ? 59 : 0,
    endOfDay ? 59 : 0,
  );
  return Number.isNaN(date.getTime()) ? null : date.toISOString();
}

function oneYearFromToday() {
  const date = new Date();
  date.setFullYear(date.getFullYear() + 1);
  return date;
}

export function BookingsPage() {
  const { user } = useAuth();
  return user?.roles.includes('ROLE_ADMIN') ? <AdminBookingsPage /> : <UserBookingsPage />;
}

function UserBookingsPage() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<Tab>('events');
  const [bookings, setBookings] = useState<Prenotazione[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [eventResults, setEventResults] = useState<Evento[]>([]);
  const [endDate, setEndDate] = useState(() => toInputDate(oneYearFromToday()));
  const [eventName, setEventName] = useState('');
  const [eventSortMode, setEventSortMode] = useState<SortMode>('dateAsc');
  const [bookingSortMode, setBookingSortMode] = useState<SortMode>('dateAsc');
  const [loading, setLoading] = useState(true);
  const [loadingEvents, setLoadingEvents] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [fatalError, setFatalError] = useState<Error | null>(null);
  const [modal, setModal] = useState<ModalState>(null);
  const [result, setResult] = useState<ResultState>(null);
  const [busy, setBusy] = useState(false);
  const featureEnabled = appConfig.featureFlags.bookings;

  const today = useMemo(() => {
    const date = new Date();
    date.setHours(0, 0, 0, 0);
    return date;
  }, []);
  const todayLabel = toInputDate(today);
  const futureBookings = useMemo(
    () =>
      bookings
        .filter((booking) => {
          const event = events.find((item) => item.idEvento === booking.idEvento);
          return event ? isFutureDate(event.data) : false;
        })
        .sort((a, b) => {
          const eventA = events.find((event) => event.idEvento === a.idEvento);
          const eventB = events.find((event) => event.idEvento === b.idEvento);
          if (bookingSortMode === 'nameAsc' || bookingSortMode === 'nameDesc') {
            const comparison = (eventA?.nome ?? '').localeCompare(eventB?.nome ?? '', 'it');
            return bookingSortMode === 'nameAsc' ? comparison : -comparison;
          }
          const comparison = new Date(eventA?.data ?? 0).getTime() - new Date(eventB?.data ?? 0).getTime();
          return bookingSortMode === 'dateAsc' ? comparison : -comparison;
        }),
    [bookings, bookingSortMode, events],
  );
  const availableEvents = useMemo(() => {
    const bookedIds = new Set(bookings.map((booking) => booking.idEvento));
    return eventResults
      .filter((event) => !bookedIds.has(event.idEvento))
      .sort((a, b) =>
        eventSortMode === 'nameAsc' || eventSortMode === 'nameDesc'
          ? (eventSortMode === 'nameAsc' ? 1 : -1) * a.nome.localeCompare(b.nome, 'it')
          : (eventSortMode === 'dateAsc' ? 1 : -1) * (new Date(a.data).getTime() - new Date(b.data).getTime()),
      );
  }, [bookings, eventResults, eventSortMode]);

  const handleError = (caughtError: unknown) => {
    if (caughtError instanceof ApiError && caughtError.status >= 500) {
      setFatalError(caughtError);
      return;
    }
    setError(caughtError instanceof Error ? caughtError.message : strings.errors.unknown);
  };

  const loadUserBookings = async () => {
    if (!user) return [];
    const nextBookings = await getBookingsByUserId(user.idUtente);
    setBookings(nextBookings);
    return nextBookings;
  };

  const loadEvents = async (name = eventName, selectedEnd = endDate) => {
    const end = toIsoDate(selectedEnd, true);
    if (!end || new Date(end).getTime() < today.getTime()) {
      setError(strings.bookings.endDateError);
      return;
    }
    setLoadingEvents(true);
    try {
      const start = today.toISOString();
      const namedEvents = name.trim() ? await getEventsByName(name.trim()) : null;
      const nextEvents = namedEvents ?? (await getEventsByDate(start, end));
      const filtered = nextEvents.filter((event) => {
        const eventTime = new Date(event.data).getTime();
        return eventTime >= today.getTime() && eventTime <= new Date(end).getTime();
      });
      setEventResults(filtered);
      setError(null);
    } catch (caughtError) {
      handleError(caughtError);
    } finally {
      setLoadingEvents(false);
    }
  };

  const refresh = async () => {
    if (!user) return;
    setLoading(true);
    try {
      await Promise.all([loadUserBookings(), getEvents()]).then(([, allEvents]) => setEvents(allEvents));
      await loadEvents();
      setError(null);
    } catch (caughtError) {
      handleError(caughtError);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!featureEnabled || !user) return;
    void refresh();
  }, [featureEnabled, user]);

  if (fatalError) return <GenericServerErrorPage error={fatalError} />;
  if (!featureEnabled) return <NotFoundPage />;

  const handleActionError = (caughtError: unknown, keepModal = false) => {
    if (caughtError instanceof ApiError && caughtError.status >= 500) {
      setFatalError(caughtError);
      return;
    }
    setResult({ success: false, message: caughtError instanceof Error ? caughtError.message : strings.errors.unknown });
    if (!keepModal) setModal(null);
  };

  const eventForBooking = (booking: Prenotazione) => events.find((event) => event.idEvento === booking.idEvento);

  return (
    <ProtectedLayout>
      <div className="space-y-6">
        <h1 className="text-3xl font-semibold text-white">{strings.bookings.title}</h1>
        <div className="flex flex-wrap items-center justify-between gap-3 border-b border-white/10">
          <div className="flex gap-2">
            <button
              type="button"
              className={`border-b-2 px-4 py-3 text-sm ${activeTab === 'events' ? 'border-cyan-300 text-cyan-200' : 'border-transparent text-slate-400'}`}
              onClick={() => setActiveTab('events')}
            >
              {strings.bookings.eventsTab}
            </button>
            <button
              type="button"
              className={`border-b-2 px-4 py-3 text-sm ${activeTab === 'bookings' ? 'border-cyan-300 text-cyan-200' : 'border-transparent text-slate-400'}`}
              onClick={() => setActiveTab('bookings')}
            >
              {strings.bookings.bookingsTab}
            </button>
          </div>
          <Button variant="secondary" onClick={() => void refresh()}>
            {strings.bookings.refresh}
          </Button>
        </div>

        {activeTab === 'events' ? (
          <>
            <div className="grid gap-3 rounded-xl border border-white/10 bg-white/5 p-4 md:grid-cols-3">
              <label className="text-sm text-slate-300">
                {strings.bookings.endDate}
                <input value={endDate} onChange={(event) => setEndDate(event.target.value)} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" />
              </label>
              <label className="text-sm text-slate-300">
                {strings.bookings.eventName}
                <input value={eventName} onChange={(event) => setEventName(event.target.value)} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" />
              </label>
              <label className="text-sm text-slate-300">
                {strings.bookings.sortLabel}
                <select value={eventSortMode} onChange={(event) => setEventSortMode(event.target.value as SortMode)} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white">
                  <option value="dateAsc">{strings.bookings.sortDateAsc}</option>
                  <option value="dateDesc">{strings.bookings.sortDateDesc}</option>
                  <option value="nameAsc">{strings.bookings.sortNameAsc}</option>
                  <option value="nameDesc">{strings.bookings.sortNameDesc}</option>
                </select>
              </label>
              <Button className="md:col-span-3" onClick={() => void loadEvents()}>{strings.actions.filter}</Button>
            </div>
            {error ? <InlineError message={error} /> : null}
            {loadingEvents ? <LoadingState title={strings.appName} /> : null}
            {!loadingEvents && availableEvents.length === 0 ? <EmptyState title={strings.bookings.emptyState} /> : null}
            <div className="grid gap-4 md:grid-cols-2">
              {availableEvents.map((event) => (
                <EventCard key={event.idEvento} evento={event} booked={false} hasPastReservation={false} onBook={() => setModal({ type: 'create', evento: event })} />
              ))}
            </div>
          </>
        ) : (
          <>
            <div className="flex justify-end">
              <label className="w-full text-sm text-slate-300 sm:w-72">
                {strings.bookings.sortLabel}
                <select value={bookingSortMode} onChange={(event) => setBookingSortMode(event.target.value as SortMode)} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white">
                  <option value="dateAsc">{strings.bookings.sortDateAsc}</option>
                  <option value="dateDesc">{strings.bookings.sortDateDesc}</option>
                  <option value="nameAsc">{strings.bookings.sortNameAsc}</option>
                  <option value="nameDesc">{strings.bookings.sortNameDesc}</option>
                </select>
              </label>
            </div>
            {error ? <InlineError message={error} /> : null}
            {loading ? <LoadingState title={strings.appName} /> : null}
            {!loading && futureBookings.length === 0 ? <EmptyState title={strings.bookings.bookingsEmpty} /> : null}
            <div className="grid gap-4 md:grid-cols-2">
              {futureBookings.map((booking) => {
                const event = eventForBooking(booking);
                if (!event) return null;
                return (
                  <EventCard
                    key={booking.idPrenotazione}
                    evento={event}
                    booked
                    hasPastReservation={false}
                    onEdit={() => setModal({ type: 'edit', evento: event, booking })}
                    onDelete={() => setModal({ type: 'delete', booking, evento: event })}
                  />
                );
              })}
            </div>
          </>
        )}

        {modal?.type === 'create' ? (
          <BookingModal
            title={`${strings.bookings.book}: ${modal.evento.nome}`}
            confirmLabel={strings.modal.confirm}
            onClose={() => setModal(null)}
            onConfirm={async (participants) => {
              if (!user) return;
              setBusy(true);
              try {
                await createBooking({ idUtente: user.idUtente, idEvento: modal.evento.idEvento, numeroPartecipanti: participants });
                await refresh();
                setModal(null);
                setResult({ success: true, message: strings.bookings.createSuccess });
              } catch (caughtError) {
                handleActionError(caughtError, true);
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
                await refresh();
                setModal(null);
                setResult({ success: true, message: strings.bookings.updateSuccess });
              } catch (caughtError) {
                handleActionError(caughtError, true);
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}
        {modal?.type === 'delete' ? (
          <DeleteConfirmModal
            title={strings.bookings.delete}
            message={strings.modal.deleteQuestion}
            busy={busy}
            onClose={() => setModal(null)}
            onConfirm={async () => {
              setBusy(true);
              try {
                await deleteBooking(modal.booking.idPrenotazione);
                await refresh();
                setModal(null);
                setResult({ success: true, message: strings.bookings.deleteSuccess });
              } catch (caughtError) {
                handleActionError(caughtError, true);
              } finally {
                setBusy(false);
              }
            }}
          />
        ) : null}
        {result ? (
          <ResultModal
            title={result.success ? strings.bookings.operationSuccess : strings.bookings.operationError}
            message={result.message}
            onOk={() => setResult(null)}
          />
        ) : null}
        {busy ? <LoadingState title={strings.appName} /> : null}
      </div>
    </ProtectedLayout>
  );
}
