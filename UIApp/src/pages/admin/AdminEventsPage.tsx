import { useEffect, useMemo, useState } from 'react';
import { Button } from '../../components/ui/Button';
import { Modal } from '../../components/ui/Modal';
import { DeleteConfirmModal } from '../../components/forms/DeleteConfirmModal';
import { ResultModal } from '../../components/forms/ResultModal';
import { InlineError } from '../../components/status/InlineError';
import { LoadingState } from '../../components/status/LoadingState';
import { NotFoundPage } from '../NotFoundPage';
import { strings } from '../../config/strings';
import {
  createEvent,
  deleteEvent,
  getEventById,
  getEventsByDate,
  getEventsByName,
  getAvailableBookingsByEvent,
  updateEvent,
} from '../../lib/museumApi';
import type { Evento } from '../../lib/types';
import { useAuth } from '../../state/AuthContext';
import { ProtectedLayout } from '../../components/layout/ProtectedLayout';
import { formatDateTime } from '../../lib/date';
import { AvailabilityProgress } from '../../components/event/AvailabilityProgress';

type EventDraft = { nome: string; numeroPosti: number; data: string; durata: number };
type EventSortKey = 'idEvento' | 'nome' | 'numeroPosti' | 'data' | 'durata';
type SortDirection = 'asc' | 'desc';
type EventPreferences = {
  from: string;
  to: string;
  id: string;
  name: string;
  sortKey: EventSortKey | null;
  sortDirection: SortDirection | null;
};

const emptyDraft: EventDraft = { nome: '', numeroPosti: 1, data: '', durata: 1 };
const preferencesKey = 'admin-events-preferences';

function formatFilterDate(date: Date) {
  return `${String(date.getDate()).padStart(2, '0')}-${String(date.getMonth() + 1).padStart(2, '0')}-${date.getFullYear()}`;
}

function defaultPreferences(): EventPreferences {
  const date = new Date();
  date.setFullYear(date.getFullYear() + 1);
  return {
    from: formatFilterDate(new Date()),
    to: formatFilterDate(date),
    id: '',
    name: '',
    sortKey: 'data',
    sortDirection: 'asc',
  };
}

function loadPreferences(): EventPreferences {
  const defaults = defaultPreferences();
  if (typeof window === 'undefined') return defaults;

  const stored = window.localStorage.getItem(preferencesKey);
  if (!stored) return defaults;

  try {
    const parsed = JSON.parse(stored) as Partial<EventPreferences>;
    const validSortKey: EventSortKey[] = ['idEvento', 'nome', 'numeroPosti', 'data', 'durata'];
    const validDirection: SortDirection[] = ['asc', 'desc'];
    const storedSortKey =
      parsed.sortKey === null
        ? null
        : validSortKey.includes(parsed.sortKey as EventSortKey)
          ? parsed.sortKey as EventSortKey
          : defaults.sortKey;
    const storedSortDirection =
      parsed.sortDirection === null
        ? null
        : validDirection.includes(parsed.sortDirection as SortDirection)
          ? parsed.sortDirection as SortDirection
          : defaults.sortDirection;
    return {
      ...defaults,
      ...parsed,
      sortKey: storedSortKey,
      sortDirection: storedSortDirection,
    };
  } catch {
    return defaults;
  }
}

function toIsoDate(value: string, end = false) {
  const match = /^(\d{2})-(\d{2})-(\d{4})$/.exec(value);
  if (!match) return null;
  const date = new Date(Number(match[3]), Number(match[2]) - 1, Number(match[1]), end ? 23 : 0, end ? 59 : 0, end ? 59 : 0);
  return Number.isNaN(date.getTime()) ? null : date.toISOString();
}

function EventModal({
  initial,
  title,
  onClose,
  onConfirm,
}: {
  initial: EventDraft;
  title: string;
  onClose: () => void;
  onConfirm: (draft: EventDraft) => void;
}) {
  const [draft, setDraft] = useState(initial);
  const field = (label: string, key: keyof EventDraft, type = 'text') => (
    <label className="block text-sm text-slate-200">
      {label}
      <input
        type={type}
        value={draft[key]}
        min={type === 'number' ? 1 : undefined}
        onChange={(event) =>
          setDraft((current) => ({ ...current, [key]: type === 'number' ? Number(event.target.value) : event.target.value }))
        }
        className="mt-1 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white focus:border-cyan-400"
      />
    </label>
  );
  return (
    <Modal
      title={title}
      onClose={onClose}
      actions={
        <>
          <Button variant="secondary" onClick={onClose}>{strings.modal.cancel}</Button>
          <Button onClick={() => onConfirm(draft)} disabled={!draft.nome.trim() || !draft.data}>{strings.modal.save}</Button>
        </>
      }
    >
      <div className="space-y-3">
        {field(strings.admin.name, 'nome')}
        {field(strings.admin.seats, 'numeroPosti', 'number')}
        {field(strings.admin.date, 'data', 'datetime-local')}
        {field(strings.admin.duration, 'durata', 'number')}
      </div>
    </Modal>
  );
}

export function AdminEventsPage() {
  const { user } = useAuth();
  const initialPreferences = useMemo(loadPreferences, []);
  const [rows, setRows] = useState<Evento[]>([]);
  const [from, setFrom] = useState(initialPreferences.from);
  const [to, setTo] = useState(initialPreferences.to);
  const [id, setId] = useState(initialPreferences.id);
  const [name, setName] = useState(initialPreferences.name);
  const [sortKey, setSortKey] = useState<EventSortKey | null>(initialPreferences.sortKey);
  const [sortDirection, setSortDirection] = useState<SortDirection | null>(initialPreferences.sortDirection);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [modal, setModal] = useState<{ mode: 'create' | 'edit'; event?: Evento } | null>(null);
  const [deleteRow, setDeleteRow] = useState<Evento | null>(null);
  const [result, setResult] = useState<{ title: string; message: string } | null>(null);
  const [availability, setAvailability] = useState<Record<number, { available: number; total: number } | null>>({});
  const [availabilityLoading, setAvailabilityLoading] = useState<Record<number, boolean>>({});

  useEffect(() => {
    window.localStorage.setItem(
      preferencesKey,
      JSON.stringify({ from, to, id, name, sortKey, sortDirection } satisfies EventPreferences),
    );
  }, [from, to, id, name, sortKey, sortDirection]);

  const sortedRows = useMemo(() => {
    if (!sortKey || !sortDirection) return rows;
    return [...rows].sort((left, right) => {
      let comparison: number;
      if (sortKey === 'nome') comparison = left.nome.localeCompare(right.nome, 'it');
      else if (sortKey === 'data') comparison = new Date(left.data).getTime() - new Date(right.data).getTime();
      else comparison = left[sortKey] - right[sortKey];
      return sortDirection === 'asc' ? comparison : -comparison;
    });
  }, [rows, sortDirection, sortKey]);

  const changeSort = (column: EventSortKey) => {
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

  const load = async () => {
    const start = toIsoDate(from);
    const end = toIsoDate(to, true);
    if (!start || !end) {
      setError(strings.admin.dateFormatError);
      return;
    }
    setLoading(true);
    try {
      let next = await getEventsByDate(start, end);
      if (id.trim()) {
        const event = await getEventById(Number(id));
        next = [event];
      }
      if (name.trim()) next = await getEventsByName(name.trim());
      setRows(next.filter((event) => (!id || String(event.idEvento).includes(id)) && (!name || event.nome.toLowerCase().includes(name.toLowerCase()))));
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : strings.admin.operationError);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { void load(); }, []);

  useEffect(() => {
    let active = true;
    const eventIds = sortedRows.map((event) => event.idEvento);
    setAvailabilityLoading(Object.fromEntries(eventIds.map((idEvento) => [idEvento, true])));
    void Promise.all(
      eventIds.map(async (idEvento) => {
        try {
          const response = await getAvailableBookingsByEvent(idEvento);
          return [idEvento, { available: response.numeroPostiDispobili, total: response.numeroPostiTotali }] as const;
        } catch {
          return [idEvento, null] as const;
        }
      }),
    ).then((entries) => {
      if (!active) return;
      setAvailability(Object.fromEntries(entries));
      setAvailabilityLoading(Object.fromEntries(eventIds.map((idEvento) => [idEvento, false])));
    });
    return () => {
      active = false;
    };
  }, [sortedRows]);
  if (!user?.roles.includes('ROLE_ADMIN')) return <ProtectedLayout><NotFoundPage /></ProtectedLayout>;

  const save = async (draft: EventDraft) => {
    try {
      if (modal?.mode === 'edit' && modal.event) {
        await updateEvent(modal.event.idEvento, { ...draft, idEvento: modal.event.idEvento, data: new Date(draft.data).toISOString() });
      } else {
        await createEvent({ ...draft, data: new Date(draft.data).toISOString() });
      }
      setModal(null);
      setResult({ title: strings.admin.eventsTitle, message: strings.admin.operationSuccess });
      await load();
    } catch (caughtError) {
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const remove = async () => {
    if (!deleteRow) return;
    try {
      await deleteEvent(deleteRow.idEvento);
      setDeleteRow(null);
      setResult({ title: strings.admin.eventsTitle, message: strings.admin.operationSuccess });
      await load();
    } catch (caughtError) {
      setDeleteRow(null);
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  return (
    <ProtectedLayout>
      <div className="space-y-5">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <h1 className="text-3xl font-semibold text-white">{strings.admin.eventsTitle}</h1>
          <Button onClick={() => setModal({ mode: 'create' })}>{strings.admin.newEvent}</Button>
        </div>
        <div className="grid gap-3 rounded-xl border border-white/10 bg-white/5 p-4 md:grid-cols-4">
          <label className="text-sm text-slate-300">{strings.admin.dateFrom}<input value={from} onChange={(e) => setFrom(e.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white" /></label>
          <label className="text-sm text-slate-300">{strings.admin.dateTo}<input value={to} onChange={(e) => setTo(e.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white" /></label>
          <label className="text-sm text-slate-300">{strings.admin.idEvent}<input value={id} onChange={(e) => setId(e.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white" /></label>
          <label className="text-sm text-slate-300">{strings.admin.name}<input value={name} onChange={(e) => setName(e.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white" /></label>
          <Button className="md:col-span-4" onClick={() => void load()}>{strings.actions.filter}</Button>
        </div>
        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}
        {!loading && sortedRows.length === 0 ? <p className="text-slate-400">{strings.admin.noResults}</p> : null}
        {sortedRows.length > 0 ? (
          <div className="overflow-x-auto rounded-xl border border-white/10">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="bg-white/10 text-xs uppercase text-slate-200">
                <tr>
                  {([
                    [strings.admin.idEvent, 'idEvento'],
                    [strings.admin.name, 'nome'],
                    [strings.admin.seats, 'numeroPosti'],
                    [strings.admin.date, 'data'],
                    [strings.admin.duration, 'durata'],
                  ] as [string, EventSortKey][]).map(([head, column]) => (
                    <th key={column} className="px-4 py-3">
                      <button
                        type="button"
                        onClick={() => changeSort(column)}
                        className="inline-flex items-center gap-2 text-left hover:text-white"
                        aria-label={`${head}: ${
                          sortKey !== column || !sortDirection
                            ? strings.admin.sortAscending
                            : sortDirection === 'asc'
                              ? strings.admin.sortDescending
                              : strings.actions.reset
                        }`}
                      >
                        {head}
                        <span aria-hidden="true">{sortKey === column && sortDirection ? (sortDirection === 'asc' ? '↑' : '↓') : '↕'}</span>
                      </button>
                    </th>
                  ))}
                  <th className="normal-case px-4 py-3">{strings.bookings.availability}</th>
                  <th className="px-4 py-3" />
                </tr>
              </thead>
              <tbody>{sortedRows.map((event) => <tr key={event.idEvento} className="border-t border-white/10"><td className="px-4 py-3">{event.idEvento}</td><td className="px-4 py-3">{event.nome}</td><td className="px-4 py-3">{event.numeroPosti}</td><td className="px-4 py-3">{formatDateTime(event.data)}</td><td className="px-4 py-3">{event.durata}</td><td className="px-4 py-3"><AvailabilityProgress availability={availability[event.idEvento] ?? null} loading={availabilityLoading[event.idEvento] ?? true} size="small" showLabel={false} /></td><td className="px-4 py-3"><div className="flex gap-2"><Button onClick={() => setModal({ mode: 'edit', event })}>{strings.actions.edit}</Button><Button variant="danger" onClick={() => setDeleteRow(event)}>{strings.actions.delete}</Button></div></td></tr>)}</tbody>
            </table>
          </div>
        ) : null}
        <div className="flex justify-end">
          <Button onClick={() => setModal({ mode: 'create' })}>{strings.admin.newEvent}</Button>
        </div>
        {modal ? <EventModal title={modal.mode === 'create' ? strings.admin.newEvent : strings.actions.edit} initial={modal.event ? { nome: modal.event.nome, numeroPosti: modal.event.numeroPosti, data: modal.event.data.slice(0, 16), durata: modal.event.durata } : emptyDraft} onClose={() => setModal(null)} onConfirm={(draft) => void save(draft)} /> : null}
        {deleteRow ? <DeleteConfirmModal title={strings.actions.delete} message={strings.modal.deleteQuestionEvent} onClose={() => setDeleteRow(null)} onConfirm={() => void remove()} /> : null}
        {result ? <ResultModal title={result.title} message={result.message} onOk={() => setResult(null)} /> : null}
      </div>
    </ProtectedLayout>
  );
}
