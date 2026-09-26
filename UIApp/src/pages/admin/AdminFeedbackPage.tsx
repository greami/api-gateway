import { useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import { Button } from '../../components/ui/Button';
import { DeleteConfirmModal } from '../../components/forms/DeleteConfirmModal';
import { ResultModal } from '../../components/forms/ResultModal';
import { InlineError } from '../../components/status/InlineError';
import { LoadingState } from '../../components/status/LoadingState';
import { NotFoundPage } from '../NotFoundPage';
import { ProtectedLayout } from '../../components/layout/ProtectedLayout';
import { strings } from '../../config/strings';
import {
  deleteFeedbackEvent,
  deleteFeedbackOpera,
  getFeedbacksByEventId,
  getFeedbacksByUserId,
  getFeedbacksOpereByOperaId,
  getFeedbacksOpereByUserId,
} from '../../lib/museumApi';
import type { FeedbackEvento, FeedbackOpera } from '../../lib/types';
import { ApiError } from '../../lib/apiFetch';
import { useAuth } from '../../state/AuthContext';

type Tab = 'events' | 'works';
type Direction = 'asc' | 'desc';
type EventSortKey = keyof Pick<FeedbackEvento, 'idFeedback' | 'idEvento' | 'idUtente' | 'voto' | 'note'>;
type WorkSortKey = keyof Pick<FeedbackOpera, 'idFeedback' | 'idOpera' | 'idUtente' | 'voto' | 'note'>;

function nextSort<T extends string>(currentKey: T | null, currentDirection: Direction | null, key: T, setKey: (value: T | null) => void, setDirection: (value: Direction | null) => void) {
  if (currentKey !== key) {
    setKey(key);
    setDirection('asc');
  } else if (currentDirection === 'asc') setDirection('desc');
  else if (currentDirection === 'desc') {
    setKey(null);
    setDirection(null);
  } else setDirection('asc');
}

function compareValue(left: unknown, right: unknown) {
  if (typeof left === 'string' && typeof right === 'string') return left.localeCompare(right, 'it');
  return Number(left) - Number(right);
}

export function AdminFeedbackPage() {
  const { user } = useAuth();
  const [tab, setTab] = useState<Tab>('events');
  const [eventRows, setEventRows] = useState<FeedbackEvento[]>([]);
  const [workRows, setWorkRows] = useState<FeedbackOpera[]>([]);
  const [eventId, setEventId] = useState('');
  const [eventUserId, setEventUserId] = useState('');
  const [workId, setWorkId] = useState('');
  const [workUserId, setWorkUserId] = useState('');
  const [eventSortKey, setEventSortKey] = useState<EventSortKey | null>('idFeedback');
  const [eventSortDirection, setEventSortDirection] = useState<Direction | null>('asc');
  const [workSortKey, setWorkSortKey] = useState<WorkSortKey | null>('idFeedback');
  const [workSortDirection, setWorkSortDirection] = useState<Direction | null>('asc');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [deleteRow, setDeleteRow] = useState<{ kind: Tab; id: number } | null>(null);
  const [result, setResult] = useState<{ title: string; message: string } | null>(null);

  const loadEvents = async () => {
    if (!eventId && !eventUserId) {
      setEventRows([]);
      return;
    }
    setLoading(true);
    try {
      const next = eventId ? await getFeedbacksByEventId(Number(eventId)) : await getFeedbacksByUserId(Number(eventUserId));
      setEventRows(next.filter((item) => (!eventId || String(item.idEvento) === eventId) && (!eventUserId || String(item.idUtente) === eventUserId)));
      setError(null);
    } catch (caughtError) {
      if (caughtError instanceof ApiError && caughtError.status === 403) {
        setEventRows([]);
        setError(null);
      } else setError(caughtError instanceof Error ? caughtError.message : strings.admin.operationError);
    } finally {
      setLoading(false);
    }
  };

  const loadWorks = async () => {
    if (!workId && !workUserId) {
      setWorkRows([]);
      return;
    }
    setLoading(true);
    try {
      const next = workId ? await getFeedbacksOpereByOperaId(Number(workId)) : await getFeedbacksOpereByUserId(Number(workUserId));
      setWorkRows(next.filter((item) => (!workId || String(item.idOpera) === workId) && (!workUserId || String(item.idUtente) === workUserId)));
      setError(null);
    } catch (caughtError) {
      if (caughtError instanceof ApiError && caughtError.status === 403) {
        setWorkRows([]);
        setError(null);
      } else setError(caughtError instanceof Error ? caughtError.message : strings.admin.operationError);
    } finally {
      setLoading(false);
    }
  };

  const sortedEvents = useMemo(() => {
    if (!eventSortKey || !eventSortDirection) return eventRows;
    return [...eventRows].sort((left, right) => {
      const comparison = compareValue(left[eventSortKey], right[eventSortKey]);
      return eventSortDirection === 'asc' ? comparison : -comparison;
    });
  }, [eventRows, eventSortDirection, eventSortKey]);
  const sortedWorks = useMemo(() => {
    if (!workSortKey || !workSortDirection) return workRows;
    return [...workRows].sort((left, right) => {
      const comparison = compareValue(left[workSortKey], right[workSortKey]);
      return workSortDirection === 'asc' ? comparison : -comparison;
    });
  }, [workRows, workSortDirection, workSortKey]);

  const remove = async () => {
    if (!deleteRow) return;
    try {
      if (deleteRow.kind === 'events') await deleteFeedbackEvent(deleteRow.id);
      else await deleteFeedbackOpera(deleteRow.id);
      setDeleteRow(null);
      setResult({ title: strings.admin.feedbackTitle, message: strings.admin.operationSuccess });
      await (tab === 'events' ? loadEvents() : loadWorks());
    } catch (caughtError) {
      setDeleteRow(null);
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  if (!user?.roles.includes('ROLE_ADMIN')) return <ProtectedLayout><NotFoundPage /></ProtectedLayout>;

  const eventColumns: [string, EventSortKey][] = [[strings.admin.idFeedback, 'idFeedback'], [strings.admin.idEvent, 'idEvento'], [strings.admin.idUser, 'idUtente'], [strings.admin.rating, 'voto'], [strings.admin.note, 'note']];
  const workColumns: [string, WorkSortKey][] = [[strings.admin.idFeedback, 'idFeedback'], [strings.admin.idWork, 'idOpera'], [strings.admin.idUser, 'idUtente'], [strings.admin.rating, 'voto'], [strings.admin.note, 'note']];

  return (
    <ProtectedLayout>
      <div className="space-y-5">
        <h1 className="text-3xl font-semibold text-white">{strings.admin.feedbackTitle}</h1>
        <div className="flex gap-2 border-b border-white/10">
          <button type="button" onClick={() => setTab('events')} className={`border-b-2 px-4 py-3 ${tab === 'events' ? 'border-cyan-400 text-cyan-300' : 'border-transparent text-slate-400'}`}>{strings.feedback.adminEventsTab}</button>
          <button type="button" onClick={() => setTab('works')} className={`border-b-2 px-4 py-3 ${tab === 'works' ? 'border-cyan-400 text-cyan-300' : 'border-transparent text-slate-400'}`}>{strings.feedback.adminWorksTab}</button>
        </div>
        {tab === 'events' ? (
          <FeedbackSection
            firstLabel={strings.admin.idEvent}
            firstValue={eventId}
            onFirstChange={setEventId}
            secondLabel={strings.admin.idUser}
            secondValue={eventUserId}
            onSecondChange={setEventUserId}
            onFilter={() => void loadEvents()}
            onReset={() => { setEventId(''); setEventUserId(''); setEventRows([]); }}
            columns={eventColumns}
            sortKey={eventSortKey}
            sortDirection={eventSortDirection}
            onSort={(key) => nextSort(eventSortKey, eventSortDirection, key, setEventSortKey, setEventSortDirection)}
            rows={sortedEvents}
            renderRow={(row) => <><td className="px-4 py-3">{row.idFeedback}</td><td className="px-4 py-3">{row.idEvento}</td><td className="px-4 py-3">{row.idUtente}</td><td className="px-4 py-3">{row.voto}</td><td className="px-4 py-3">{row.note}</td><td className="px-4 py-3"><Button variant="danger" onClick={() => setDeleteRow({ kind: 'events', id: row.idFeedback })}>{strings.actions.delete}</Button></td></>}
          />
        ) : (
          <FeedbackSection
            firstLabel={strings.admin.idWork}
            firstValue={workId}
            onFirstChange={setWorkId}
            secondLabel={strings.admin.idUser}
            secondValue={workUserId}
            onSecondChange={setWorkUserId}
            onFilter={() => void loadWorks()}
            onReset={() => { setWorkId(''); setWorkUserId(''); setWorkRows([]); }}
            columns={workColumns}
            sortKey={workSortKey}
            sortDirection={workSortDirection}
            onSort={(key) => nextSort(workSortKey, workSortDirection, key, setWorkSortKey, setWorkSortDirection)}
            rows={sortedWorks}
            renderRow={(row) => <><td className="px-4 py-3">{row.idFeedback}</td><td className="px-4 py-3">{row.idOpera}</td><td className="px-4 py-3">{row.idUtente}</td><td className="px-4 py-3">{row.voto}</td><td className="px-4 py-3">{row.note}</td><td className="px-4 py-3"><Button variant="danger" onClick={() => setDeleteRow({ kind: 'works', id: row.idFeedback })}>{strings.actions.delete}</Button></td></>}
          />
        )}
        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}
        {deleteRow ? <DeleteConfirmModal title={strings.actions.delete} message={strings.modal.deleteQuestionFeedback} onClose={() => setDeleteRow(null)} onConfirm={() => void remove()} /> : null}
        {result ? <ResultModal title={result.title} message={result.message} onOk={() => setResult(null)} /> : null}
      </div>
    </ProtectedLayout>
  );
}

function FeedbackSection<T extends { idFeedback: number }>({
  firstLabel, firstValue, onFirstChange, secondLabel, secondValue, onSecondChange, onFilter, onReset, columns, sortKey, sortDirection, onSort, rows, renderRow,
}: {
  firstLabel: string; firstValue: string; onFirstChange: (value: string) => void;
  secondLabel: string; secondValue: string; onSecondChange: (value: string) => void;
  onFilter: () => void; onReset: () => void; columns: [string, keyof T][];
  sortKey: keyof T | null; sortDirection: Direction | null;
  onSort: (key: keyof T) => void; rows: T[]; renderRow: (row: T) => ReactNode;
}) {
  return <div className="space-y-4">
    <div className="grid gap-3 rounded-xl border border-white/10 bg-white/5 p-4 md:grid-cols-2">
      <label className="text-sm text-slate-300">{firstLabel}<input type="text" value={firstValue} onChange={(event) => onFirstChange(event.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white" /></label>
      <label className="text-sm text-slate-300">{secondLabel}<input type="text" value={secondValue} onChange={(event) => onSecondChange(event.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white" /></label>
      <div className="flex gap-2 md:col-span-2"><Button onClick={onFilter} disabled={!firstValue && !secondValue}>{strings.actions.filter}</Button><Button variant="secondary" onClick={onReset}>{strings.actions.reset}</Button></div>
    </div>
    {!rows.length ? <p className="text-slate-400">{strings.admin.emptyFilters}</p> : <div className="overflow-x-auto rounded-xl border border-white/10"><table className="w-full text-left text-sm text-slate-300"><thead className="bg-white/10 text-xs uppercase text-slate-200"><tr>{columns.map(([head, key]) => <th key={String(key)} className="px-4 py-3"><button type="button" onClick={() => onSort(key)} className="inline-flex items-center gap-2 text-left hover:text-white">{head}<span aria-hidden="true">{sortKey === key && sortDirection ? (sortDirection === 'asc' ? '↑' : '↓') : '↕'}</span></button></th>)}<th className="px-4 py-3" /></tr></thead><tbody>{rows.map((row) => <tr key={row.idFeedback} className="border-t border-white/10">{renderRow(row)}</tr>)}</tbody></table></div>}
  </div>;
}
