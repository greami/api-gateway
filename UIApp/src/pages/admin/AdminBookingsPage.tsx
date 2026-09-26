import { useEffect, useMemo, useState } from 'react';
import { Button } from '../../components/ui/Button';
import { Modal } from '../../components/ui/Modal';
import { DeleteConfirmModal } from '../../components/forms/DeleteConfirmModal';
import { ResultModal } from '../../components/forms/ResultModal';
import { InlineError } from '../../components/status/InlineError';
import { LoadingState } from '../../components/status/LoadingState';
import { NotFoundPage } from '../NotFoundPage';
import { ProtectedLayout } from '../../components/layout/ProtectedLayout';
import { strings } from '../../config/strings';
import { createBooking, deleteBooking, getBookingById, getBookingsByDate, getBookingsByEvent, getEvents, updateBooking } from '../../lib/museumApi';
import type { Evento, Prenotazione } from '../../lib/types';
import { useAuth } from '../../state/AuthContext';
import { formatDateTime } from '../../lib/date';

type BookingPreferences = {
  idEvent: string;
  idBooking: string;
  idUser: string;
  sortKey: BookingSortKey | null;
  sortDirection: SortDirection | null;
};

const bookingPreferencesKey = 'admin-bookings-preferences';
type BookingSortKey = 'idPrenotazione' | 'idUtente' | 'idEvento' | 'eventName' | 'numeroPartecipanti' | 'dataUltimaModifica';
type SortDirection = 'asc' | 'desc';

function loadBookingPreferences(): BookingPreferences {
  const defaults: BookingPreferences = {
    idEvent: '',
    idBooking: '',
    idUser: '',
    sortKey: 'dataUltimaModifica',
    sortDirection: 'asc',
  };
  if (typeof window === 'undefined') return defaults;
  const stored = window.localStorage.getItem(bookingPreferencesKey);
  if (!stored) return defaults;
  try {
    const parsed = JSON.parse(stored) as Partial<BookingPreferences>;
    return {
      idEvent: typeof parsed.idEvent === 'string' ? parsed.idEvent : '',
      idBooking: typeof parsed.idBooking === 'string' ? parsed.idBooking : '',
      idUser: typeof parsed.idUser === 'string' ? parsed.idUser : '',
      sortKey: parsed.sortKey === null ? null : parsed.sortKey ?? defaults.sortKey,
      sortDirection: parsed.sortDirection === null ? null : parsed.sortDirection ?? defaults.sortDirection,
    };
  } catch {
    return defaults;
  }
}

function BookingModal({
  initial,
  events,
  onClose,
  onConfirm,
}: {
  initial: { idUtente: number; idEvento: number; numeroPartecipanti: number };
  events: Evento[];
  onClose: () => void;
  onConfirm: (value: { idUtente: number; idEvento: number; numeroPartecipanti: number }) => void;
}) {
  const [value, setValue] = useState(initial);
  return (
    <Modal title={strings.admin.bookingsTitle} onClose={onClose} actions={<><Button variant="secondary" onClick={onClose}>{strings.modal.cancel}</Button><Button onClick={() => onConfirm(value)} disabled={value.idUtente < 1 || value.idEvento < 1 || value.numeroPartecipanti < 2}>{strings.modal.save}</Button></>}>
      <div className="space-y-3">
        <label className="block text-sm text-slate-200">{strings.admin.idUser}<input type="number" value={value.idUtente} onChange={(e) => setValue({ ...value, idUtente: Number(e.target.value) })} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" /></label>
        <label className="block text-sm text-slate-200">{strings.admin.event}<select value={value.idEvento} onChange={(e) => setValue({ ...value, idEvento: Number(e.target.value) })} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white"><option value={0}>Seleziona</option>{events.map((event) => <option key={event.idEvento} value={event.idEvento}>{event.idEvento} · {event.nome}</option>)}</select></label>
        <label className="block text-sm text-slate-200">{strings.admin.participants}<input type="number" min={2} value={value.numeroPartecipanti} onChange={(e) => setValue({ ...value, numeroPartecipanti: Number(e.target.value) })} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" /></label>
      </div>
    </Modal>
  );
}

export function AdminBookingsPage() {
  const { user } = useAuth();
  const initialPreferences = useMemo(loadBookingPreferences, []);
  const [rows, setRows] = useState<Prenotazione[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [idEvent, setIdEvent] = useState(initialPreferences.idEvent);
  const [idBooking, setIdBooking] = useState(initialPreferences.idBooking);
  const [idUser, setIdUser] = useState(initialPreferences.idUser);
  const [sortKey, setSortKey] = useState<BookingSortKey | null>(initialPreferences.sortKey);
  const [sortDirection, setSortDirection] = useState<SortDirection | null>(initialPreferences.sortDirection);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [modal, setModal] = useState<{ mode: 'create' | 'edit'; booking?: Prenotazione } | null>(null);
  const [deleteRow, setDeleteRow] = useState<Prenotazione | null>(null);
  const [result, setResult] = useState<{ title: string; message: string } | null>(null);

  useEffect(() => {
    window.localStorage.setItem(bookingPreferencesKey, JSON.stringify({ idEvent, idBooking, idUser, sortKey, sortDirection }));
  }, [idEvent, idBooking, idUser, sortDirection, sortKey]);

  useEffect(() => {
    getEvents().then(setEvents).catch(() => setError(strings.admin.operationError));
  }, []);

  const load = async () => {
    if (!idEvent && !idBooking && !idUser) {
      setRows([]);
      return;
    }
    setLoading(true);
    try {
      let next: Prenotazione[] = [];
      if (idBooking) next = [await getBookingById(Number(idBooking))];
      else if (idEvent) next = await getBookingsByEvent(Number(idEvent));
      else next = await getBookingsByDate('1970-01-01T00:00:00Z', '2100-01-01T00:00:00Z');
      setRows(next.filter((item) => (!idEvent || String(item.idEvento) === idEvent) && (!idBooking || String(item.idPrenotazione) === idBooking) && (!idUser || String(item.idUtente) === idUser)));
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : strings.admin.operationError);
    } finally {
      setLoading(false);
    }
  };

  if (!user?.roles.includes('ROLE_ADMIN')) return <ProtectedLayout><NotFoundPage /></ProtectedLayout>;

  const save = async (value: { idUtente: number; idEvento: number; numeroPartecipanti: number }) => {
    try {
      if (modal?.mode === 'edit' && modal.booking) await updateBooking(modal.booking.idPrenotazione, { numeroPartecipanti: value.numeroPartecipanti });
      else await createBooking(value);
      setModal(null);
      setResult({ title: strings.admin.bookingsTitle, message: strings.admin.operationSuccess });
      await load();
    } catch (caughtError) {
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const remove = async () => {
    if (!deleteRow) return;
    try {
      await deleteBooking(deleteRow.idPrenotazione);
      setDeleteRow(null);
      setResult({ title: strings.admin.bookingsTitle, message: strings.admin.operationSuccess });
      await load();
    } catch (caughtError) {
      setDeleteRow(null);
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const eventNames = useMemo(() => new Map(events.map((event) => [event.idEvento, event.nome])), [events]);
  const sortedRows = useMemo(() => {
    if (!sortKey || !sortDirection) return rows;
    return [...rows].sort((left, right) => {
      let comparison: number;
      if (sortKey === 'eventName') {
        comparison = (eventNames.get(left.idEvento) ?? '').localeCompare(eventNames.get(right.idEvento) ?? '', 'it');
      } else if (sortKey === 'dataUltimaModifica') {
        comparison = new Date(left.dataUltimaModifica).getTime() - new Date(right.dataUltimaModifica).getTime();
      } else {
        comparison = left[sortKey] - right[sortKey];
      }
      return sortDirection === 'asc' ? comparison : -comparison;
    });
  }, [eventNames, rows, sortDirection, sortKey]);
  const changeSort = (column: BookingSortKey) => {
    if (sortKey === column) {
      if (sortDirection === 'asc') {
        setSortDirection('desc');
      } else if (sortDirection === 'desc') {
        setSortKey(null);
        setSortDirection(null);
      } else {
        setSortDirection('asc');
      }
      return;
    }
    setSortKey(column);
    setSortDirection('asc');
  };
  const hasActiveFilter = Boolean(idEvent || idBooking || idUser);
  const setBookingFilter = (value: string) => {
    setIdBooking(value);
    if (value) {
      setIdEvent('');
      setIdUser('');
    }
  };
  const setEventFilter = (value: string) => {
    setIdEvent(value);
    if (value) {
      setIdBooking('');
      setIdUser('');
    }
  };
  const setUserFilter = (value: string) => {
    setIdUser(value);
    if (value) {
      setIdEvent('');
      setIdBooking('');
    }
  };
  const resetFilters = () => {
    setIdEvent('');
    setIdBooking('');
    setIdUser('');
    setRows([]);
    setError(null);
  };

  return (
    <ProtectedLayout>
      <div className="space-y-5">
        <div className="flex flex-wrap items-center justify-between gap-3"><h1 className="text-3xl font-semibold text-white">{strings.admin.bookingsTitle}</h1><Button onClick={() => setModal({ mode: 'create' })}>{strings.admin.newBooking}</Button></div>
        <div className="grid gap-3 rounded-xl border border-white/10 bg-white/5 p-4 md:grid-cols-3">
          <label className="text-sm text-slate-300">{strings.admin.idEvent}<select value={idEvent} onChange={(e) => setEventFilter(e.target.value)} disabled={Boolean((idBooking || idUser) && !idEvent)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white disabled:cursor-not-allowed disabled:opacity-50"><option value="">{strings.admin.select}</option>{events.map((event) => <option key={event.idEvento} value={event.idEvento}>{event.idEvento} · {event.nome}</option>)}</select></label>
          <label className="text-sm text-slate-300">{strings.admin.idBooking}<input type="text" inputMode="numeric" value={idBooking} onChange={(e) => setBookingFilter(e.target.value)} disabled={Boolean((idEvent || idUser) && !idBooking)} placeholder={strings.admin.idBooking} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white placeholder:text-slate-400 disabled:cursor-not-allowed disabled:opacity-50" /></label>
          <label className="text-sm text-slate-300">{strings.admin.idUser}<input type="text" inputMode="numeric" value={idUser} onChange={(e) => setUserFilter(e.target.value)} disabled={Boolean((idEvent || idBooking) && !idUser)} placeholder={strings.admin.idUser} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white placeholder:text-slate-400 disabled:cursor-not-allowed disabled:opacity-50" /></label>
          <div className="flex gap-2 md:col-span-3">
            <Button onClick={() => void load()} disabled={!hasActiveFilter}>{strings.actions.filter}</Button>
            <Button variant="secondary" onClick={resetFilters}>{strings.actions.reset}</Button>
          </div>
        </div>
        {error ? <InlineError message={error} /> : null}{loading ? <LoadingState title={strings.appName} /> : null}
        {!loading && rows.length === 0 ? <p className="text-slate-400">{strings.admin.emptyFilters}</p> : null}
        {sortedRows.length > 0 ? <div className="overflow-x-auto rounded-xl border border-white/10"><table className="w-full text-left text-sm text-slate-300"><thead className="bg-white/10 text-xs uppercase text-slate-200"><tr>{([
          [strings.admin.idBooking, 'idPrenotazione'],
          [strings.admin.idUser, 'idUtente'],
          [strings.admin.idEvent, 'idEvento'],
          [strings.admin.eventName, 'eventName'],
          [strings.admin.participants, 'numeroPartecipanti'],
          [strings.admin.lastUpdate, 'dataUltimaModifica'],
        ] as [string, BookingSortKey][]).map(([head, column]) => <th key={column} className="px-4 py-3"><button type="button" onClick={() => changeSort(column)} className="inline-flex items-center gap-2 text-left hover:text-white" aria-label={`${head}: ${sortKey !== column || !sortDirection ? strings.admin.sortAscending : sortDirection === 'asc' ? strings.admin.sortDescending : strings.actions.reset}`}>{head}<span aria-hidden="true">{sortKey === column && sortDirection ? (sortDirection === 'asc' ? '↑' : '↓') : '↕'}</span></button></th>)}<th className="px-4 py-3" aria-hidden="true" /></tr></thead><tbody>{sortedRows.map((row) => <tr key={row.idPrenotazione} className="border-t border-white/10"><td className="px-4 py-3">{row.idPrenotazione}</td><td className="px-4 py-3">{row.idUtente}</td><td className="px-4 py-3">{row.idEvento}</td><td className="px-4 py-3">{eventNames.get(row.idEvento) ?? strings.errors.unknown}</td><td className="px-4 py-3">{row.numeroPartecipanti}</td><td className="px-4 py-3">{formatDateTime(row.dataUltimaModifica)}</td><td className="px-4 py-3"><div className="flex gap-2"><Button onClick={() => setModal({ mode: 'edit', booking: row })}>{strings.actions.edit}</Button><Button variant="danger" onClick={() => setDeleteRow(row)}>{strings.actions.delete}</Button></div></td></tr>)}</tbody></table></div> : null}
        <div className="flex justify-end">
          <Button onClick={() => setModal({ mode: 'create' })}>{strings.admin.newBooking}</Button>
        </div>
        {modal ? <BookingModal events={events} initial={modal.booking ? { idUtente: modal.booking.idUtente, idEvento: modal.booking.idEvento, numeroPartecipanti: modal.booking.numeroPartecipanti } : { idUtente: 1000, idEvento: events[0]?.idEvento ?? 0, numeroPartecipanti: 2 }} onClose={() => setModal(null)} onConfirm={(value) => void save(value)} /> : null}
        {deleteRow ? <DeleteConfirmModal title={strings.actions.delete} message={strings.modal.deleteQuestionBooking} onClose={() => setDeleteRow(null)} onConfirm={() => void remove()} /> : null}
        {result ? <ResultModal title={result.title} message={result.message} onOk={() => setResult(null)} /> : null}
      </div>
    </ProtectedLayout>
  );
}
