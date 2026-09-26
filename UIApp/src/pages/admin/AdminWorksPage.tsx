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
import {
  createOpera,
  deleteOpera,
  getOperaById,
  getOpereByAutore,
  getOpereByEvento,
  getOpereByNome,
  getEvents,
  getEventById,
  getEventiByOpera,
  createEventoOpera,
  deleteEventoOpera,
  getEventoOpereByEvento,
  updateOpera,
} from '../../lib/museumApi';
import type { Evento, Opera } from '../../lib/types';
import { useAuth } from '../../state/AuthContext';

type WorksTab = 'works' | 'eventWorks';
type WorkSortKey = 'idOpera' | 'nome' | 'autore' | 'tipo' | 'eventName';
type SortDirection = 'asc' | 'desc';
type AssociationRow = { idEvento: number; idOpera: number; eventName: string; workName: string };
type AssociationSortKey = 'idOpera' | 'workName' | 'idEvento' | 'eventName';
type Preferences = {
  tab: WorksTab;
  id: string;
  name: string;
  eventId: string;
  author: string;
  sortKey: WorkSortKey | null;
  sortDirection: SortDirection | null;
};

const preferencesKey = 'admin-works-preferences';

function loadPreferences(): Preferences {
  const defaults: Preferences = {
    tab: 'works',
    id: '',
    name: '',
    eventId: '',
    author: '',
    sortKey: 'nome',
    sortDirection: 'asc',
  };
  if (typeof window === 'undefined') return defaults;
  const stored = window.localStorage.getItem(preferencesKey);
  if (!stored) return defaults;
  try {
    const parsed = JSON.parse(stored) as Partial<Preferences>;
    return {
      ...defaults,
      ...parsed,
      tab: parsed.tab === 'eventWorks' ? 'eventWorks' : 'works',
      sortKey: parsed.sortKey === null ? null : parsed.sortKey ?? defaults.sortKey,
      sortDirection: parsed.sortDirection === null ? null : parsed.sortDirection ?? defaults.sortDirection,
    };
  } catch {
    return defaults;
  }
}

function WorkModal({
  initial,
  showType,
  onClose,
  onConfirm,
}: {
  initial: { nome: string; autore: string; tipo: number };
  showType: boolean;
  onClose: () => void;
  onConfirm: (value: { nome: string; autore: string; tipo: number }) => void;
}) {
  const [value, setValue] = useState(initial);
  return (
    <Modal
      title={strings.admin.worksTitle}
      onClose={onClose}
      actions={
        <>
          <Button variant="secondary" onClick={onClose}>{strings.modal.cancel}</Button>
          <Button onClick={() => onConfirm(value)} disabled={!value.nome.trim() || !value.autore.trim()}>{strings.modal.save}</Button>
        </>
      }
    >
      <div className="space-y-3">
        <label className="block text-sm text-slate-200">{strings.admin.name}<input value={value.nome} onChange={(e) => setValue({ ...value, nome: e.target.value })} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" /></label>
        <label className="block text-sm text-slate-200">{strings.admin.author}<input value={value.autore} onChange={(e) => setValue({ ...value, autore: e.target.value })} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" /></label>
        {showType ? <label className="block text-sm text-slate-200">{strings.admin.type}<input type="number" value={value.tipo} onChange={(e) => setValue({ ...value, tipo: Number(e.target.value) })} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" /></label> : null}
      </div>
    </Modal>
  );
}

export function AdminWorksPage() {
  const { user } = useAuth();
  const initial = useMemo(loadPreferences, []);
  const [tab, setTab] = useState<WorksTab>(initial.tab);
  const [rows, setRows] = useState<Opera[]>([]);
  const [associationRows, setAssociationRows] = useState<AssociationRow[]>([]);
  const [events, setEvents] = useState<Evento[]>([]);
  const [id, setId] = useState(initial.id);
  const [name, setName] = useState(initial.name);
  const [eventId, setEventId] = useState(initial.eventId);
  const [author, setAuthor] = useState(initial.author);
  const [sortKey, setSortKey] = useState<WorkSortKey | null>(initial.sortKey);
  const [sortDirection, setSortDirection] = useState<SortDirection | null>(initial.sortDirection);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [modal, setModal] = useState<{ mode: 'create' | 'edit'; work?: Opera } | null>(null);
  const [deleteRow, setDeleteRow] = useState<Opera | null>(null);
  const [result, setResult] = useState<{ title: string; message: string } | null>(null);
  const [associationModal, setAssociationModal] = useState(false);
  const [associationDelete, setAssociationDelete] = useState<AssociationRow | null>(null);
  const [associationSortKey, setAssociationSortKey] = useState<AssociationSortKey | null>('workName');
  const [associationSortDirection, setAssociationSortDirection] = useState<SortDirection | null>('asc');
  const [associationForm, setAssociationForm] = useState({ idEvento: '', idOpera: '' });

  const eventNames = useMemo(() => new Map(events.map((event) => [event.idEvento, event.nome])), [events]);

  useEffect(() => {
    void getEvents().then(setEvents).catch(() => setError(strings.admin.operationError));
  }, []);

  useEffect(() => {
    window.localStorage.setItem(preferencesKey, JSON.stringify({ tab, id, name, eventId, author, sortKey, sortDirection }));
  }, [author, eventId, id, name, sortDirection, sortKey, tab]);

  const activeFilter = tab === 'eventWorks' ? Boolean(id || eventId) : Boolean(id || name || author);
  const setExclusive = (field: 'id' | 'name' | 'eventId' | 'author', value: string) => {
    if (field !== 'id') setId('');
    if (field !== 'name') setName('');
    if (field !== 'eventId') setEventId('');
    if (field !== 'author') setAuthor('');
    if (field === 'id') setId(value);
    if (field === 'name') setName(value);
    if (field === 'eventId') setEventId(value);
    if (field === 'author') setAuthor(value);
  };
  const resetFilters = () => {
    setId('');
    setName('');
    setEventId('');
    setAuthor('');
    setRows([]);
    setError(null);
  };

  const load = async () => {
    if (!activeFilter) {
      setRows([]);
      return;
    }
    setLoading(true);
    try {
      let next: Opera[];
      if (id) next = [await getOperaById(Number(id))];
      else if (eventId) next = await getOpereByEvento(Number(eventId));
      else if (name) next = await getOpereByNome(name);
      else next = await getOpereByAutore(author);
      setRows(next.filter((work) =>
        (!id || String(work.idOpera) === id)
        && (!name || work.nome.toLowerCase().includes(name.toLowerCase()))
        && (!author || work.autore.toLowerCase().includes(author.toLowerCase())),
      ));
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : strings.admin.operationError);
    } finally {
      setLoading(false);
    }
  };

  const loadAssociations = async () => {
    if (!id && !eventId) {
      setAssociationRows([]);
      return;
    }
    setLoading(true);
    try {
      const associations = eventId
        ? await getEventoOpereByEvento(Number(eventId))
        : await getEventiByOpera(Number(id));
      const eventIds = [...new Set(associations.map((item) => item.idEvento))];
      const workIds = [...new Set(associations.map((item) => item.idOpera))];
      const [eventDetails, workDetails] = await Promise.all([
        Promise.all(eventIds.map(async (idEvento) => {
          const event = events.find((item) => item.idEvento === idEvento) ?? await getEventById(idEvento);
          return [idEvento, event.nome] as const;
        })),
        Promise.all(workIds.map(async (idOpera) => {
          const work = rows.find((item) => item.idOpera === idOpera) ?? await getOperaById(idOpera);
          return [idOpera, work.nome] as const;
        })),
      ]);
      const eventNamesById = new Map(eventDetails);
      const workNamesById = new Map(workDetails);
      setAssociationRows(associations.map((item) => ({
        idEvento: item.idEvento,
        idOpera: item.idOpera,
        eventName: eventNamesById.get(item.idEvento) ?? strings.errors.unknown,
        workName: workNamesById.get(item.idOpera) ?? strings.errors.unknown,
      })));
      setError(null);
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : strings.admin.operationError);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (tab === 'eventWorks' && eventId) void load();
  // The selected tab changes which result set is displayed.
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tab]);

  const sortedRows = useMemo(() => {
    if (!sortKey || !sortDirection) return rows;
    return [...rows].sort((left, right) => {
      let comparison: number;
      if (sortKey === 'nome' || sortKey === 'autore') comparison = left[sortKey].localeCompare(right[sortKey], 'it');
      else if (sortKey === 'eventName') comparison = (eventNames.get(Number(eventId)) ?? '').localeCompare(eventNames.get(Number(eventId)) ?? '', 'it');
      else comparison = left[sortKey] - right[sortKey];
      return sortDirection === 'asc' ? comparison : -comparison;
    });
  }, [eventId, eventNames, rows, sortDirection, sortKey]);

  const changeSort = (column: WorkSortKey) => {
    if (sortKey === column) {
      if (sortDirection === 'asc') setSortDirection('desc');
      else if (sortDirection === 'desc') {
        setSortKey(null);
        setSortDirection(null);
      } else setSortDirection('asc');
    } else {
      setSortKey(column);
      setSortDirection('asc');
    }
  };

  const sortedAssociations = useMemo(() => {
    if (!associationSortKey || !associationSortDirection) return associationRows;
    return [...associationRows].sort((left, right) => {
      const comparison = typeof left[associationSortKey] === 'string'
        ? String(left[associationSortKey]).localeCompare(String(right[associationSortKey]), 'it')
        : Number(left[associationSortKey]) - Number(right[associationSortKey]);
      return associationSortDirection === 'asc' ? comparison : -comparison;
    });
  }, [associationRows, associationSortDirection, associationSortKey]);

  const changeAssociationSort = (column: AssociationSortKey) => {
    if (associationSortKey === column) {
      if (associationSortDirection === 'asc') setAssociationSortDirection('desc');
      else if (associationSortDirection === 'desc') {
        setAssociationSortKey(null);
        setAssociationSortDirection(null);
      } else setAssociationSortDirection('asc');
    } else {
      setAssociationSortKey(column);
      setAssociationSortDirection('asc');
    }
  };

  if (!user?.roles.includes('ROLE_ADMIN')) return <ProtectedLayout><NotFoundPage /></ProtectedLayout>;

  const save = async (value: { nome: string; autore: string; tipo: number }) => {
    try {
      if (modal?.mode === 'edit' && modal.work) await updateOpera(modal.work.idOpera, value);
      else await createOpera(value);
      setModal(null);
      setResult({ title: strings.admin.worksTitle, message: strings.admin.operationSuccess });
      await load();
    } catch (caughtError) {
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const remove = async () => {
    if (!deleteRow) return;
    try {
      await deleteOpera(deleteRow.idOpera);
      setDeleteRow(null);
      setResult({ title: strings.admin.worksTitle, message: strings.admin.operationSuccess });
      await load();
    } catch (caughtError) {
      setDeleteRow(null);
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const saveAssociation = async () => {
    try {
      await createEventoOpera({ idEvento: Number(associationForm.idEvento), idOpera: Number(associationForm.idOpera) });
      setAssociationModal(false);
      setAssociationForm({ idEvento: '', idOpera: '' });
      setResult({ title: strings.admin.associationTab, message: strings.admin.operationSuccess });
      await loadAssociations();
    } catch (caughtError) {
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const removeAssociation = async () => {
    if (!associationDelete) return;
    try {
      await deleteEventoOpera(associationDelete.idEvento, associationDelete.idOpera);
      setAssociationDelete(null);
      setResult({ title: strings.admin.associationTab, message: strings.admin.operationSuccess });
      await loadAssociations();
    } catch (caughtError) {
      setAssociationDelete(null);
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const columns: [string, WorkSortKey][] = [
    [strings.admin.idWork, 'idOpera'],
    [strings.admin.name, 'nome'],
    [strings.admin.author, 'autore'],
  ];
  if (tab === 'eventWorks') {
    columns.push([strings.admin.type, 'tipo']);
    columns.push([strings.admin.eventName, 'eventName']);
  }

  return (
    <ProtectedLayout>
      <div className="space-y-5">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <h1 className="text-3xl font-semibold text-white">{strings.admin.worksTitle}</h1>
          <Button onClick={() => tab === 'eventWorks' ? setAssociationModal(true) : setModal({ mode: 'create' })}>{tab === 'eventWorks' ? strings.admin.associationAdd : strings.admin.newWork}</Button>
        </div>
        <div className="flex gap-2 border-b border-white/10">
          <button type="button" onClick={() => setTab('works')} className={`border-b-2 px-4 py-3 text-sm ${tab === 'works' ? 'border-cyan-400 text-cyan-300' : 'border-transparent text-slate-400'}`}>{strings.admin.worksTitle}</button>
          <button type="button" onClick={() => setTab('eventWorks')} className={`border-b-2 px-4 py-3 text-sm ${tab === 'eventWorks' ? 'border-cyan-400 text-cyan-300' : 'border-transparent text-slate-400'}`}>{strings.admin.associationTab}</button>
        </div>
        <div className="grid gap-3 rounded-xl border border-white/10 bg-white/5 p-4 md:grid-cols-4">
          <label className="text-sm text-slate-300">{strings.admin.idWork}<input type="text" value={id} onChange={(e) => setExclusive('id', e.target.value)} disabled={Boolean((name || eventId || author) && !id)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white disabled:opacity-50" /></label>
          {tab === 'works' ? <label className="text-sm text-slate-300">{strings.admin.name}<input value={name} onChange={(e) => setExclusive('name', e.target.value)} disabled={Boolean((id || eventId || author) && !name)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white disabled:opacity-50" /></label> : null}
          <label className="text-sm text-slate-300">{strings.admin.idEvent}<input type="text" value={eventId} onChange={(e) => setExclusive('eventId', e.target.value)} disabled={Boolean((id || name || author) && !eventId)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white disabled:opacity-50" /></label>
          {tab === 'works' ? <label className="text-sm text-slate-300">{strings.admin.author}<input value={author} onChange={(e) => setExclusive('author', e.target.value)} disabled={Boolean((id || name || eventId) && !author)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white disabled:opacity-50" /></label> : null}
          <div className="flex gap-2 md:col-span-4">
            <Button onClick={() => void (tab === 'eventWorks' ? loadAssociations() : load())} disabled={!activeFilter}>{strings.actions.filter}</Button>
            <Button variant="secondary" onClick={resetFilters}>{strings.actions.reset}</Button>
          </div>
        </div>
        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}
        {!loading && (tab === 'eventWorks' ? sortedAssociations.length === 0 : sortedRows.length === 0) ? <p className="text-slate-400">{strings.admin.emptyFilters}</p> : null}
        {tab === 'eventWorks' ? (
          sortedAssociations.length > 0 ? (
          <div className="overflow-x-auto rounded-xl border border-white/10">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="bg-white/10 text-xs uppercase text-slate-200">
                <tr>{([
                  [strings.admin.idWork, 'idOpera'],
                  [strings.admin.name, 'workName'],
                  [strings.admin.idEvent, 'idEvento'],
                  [strings.admin.eventName, 'eventName'],
                ] as [string, AssociationSortKey][]).map(([head, column]) => <th key={column} className="px-4 py-3"><button type="button" onClick={() => changeAssociationSort(column)} className="inline-flex items-center gap-2 text-left hover:text-white">{head}<span aria-hidden="true">{associationSortKey === column && associationSortDirection ? (associationSortDirection === 'asc' ? '↑' : '↓') : '↕'}</span></button></th>)}<th className="px-4 py-3" /></tr>
              </thead>
              <tbody>{sortedAssociations.map((row) => <tr key={`${row.idEvento}-${row.idOpera}`} className="border-t border-white/10"><td className="px-4 py-3">{row.idOpera}</td><td className="px-4 py-3">{row.workName}</td><td className="px-4 py-3">{row.idEvento}</td><td className="px-4 py-3">{row.eventName}</td><td className="px-4 py-3"><Button variant="danger" onClick={() => setAssociationDelete(row)}>{strings.actions.delete}</Button></td></tr>)}</tbody>
            </table>
          </div>
          ) : null
        ) : sortedRows.length > 0 ? (
          <div className="overflow-x-auto rounded-xl border border-white/10">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="bg-white/10 text-xs uppercase text-slate-200">
                <tr>{columns.map(([head, column]) => <th key={column} className="px-4 py-3"><button type="button" onClick={() => changeSort(column)} className="inline-flex items-center gap-2 text-left hover:text-white" aria-label={`${head}: ${sortKey !== column || !sortDirection ? strings.admin.sortAscending : sortDirection === 'asc' ? strings.admin.sortDescending : strings.actions.reset}`}>{head}<span aria-hidden="true">{sortKey === column && sortDirection ? (sortDirection === 'asc' ? '↑' : '↓') : '↕'}</span></button></th>)}<th className="px-4 py-3" /></tr>
              </thead>
              <tbody>{sortedRows.map((row) => <tr key={row.idOpera} className="border-t border-white/10"><td className="px-4 py-3">{row.idOpera}</td><td className="px-4 py-3">{row.nome}</td><td className="px-4 py-3">{row.autore}</td><td className="px-4 py-3"><div className="flex gap-2"><Button onClick={() => setModal({ mode: 'edit', work: row })}>{strings.actions.edit}</Button><Button variant="danger" onClick={() => setDeleteRow(row)}>{strings.actions.delete}</Button></div></td></tr>)}</tbody>
            </table>
          </div>
        ) : null}
        <div className="flex justify-end"><Button onClick={() => tab === 'eventWorks' ? setAssociationModal(true) : setModal({ mode: 'create' })}>{tab === 'eventWorks' ? strings.admin.associationAdd : strings.admin.newWork}</Button></div>
        {modal ? <WorkModal showType={tab === 'eventWorks'} initial={modal.work ? { nome: modal.work.nome, autore: modal.work.autore, tipo: modal.work.tipo } : { nome: '', autore: '', tipo: 1 }} onClose={() => setModal(null)} onConfirm={(value) => void save(value)} /> : null}
        {deleteRow ? <DeleteConfirmModal title={strings.actions.delete} message={strings.modal.deleteQuestionWork} onClose={() => setDeleteRow(null)} onConfirm={() => void remove()} /> : null}
        {associationDelete ? <DeleteConfirmModal title={strings.actions.delete} message={strings.admin.associationDeleteQuestion} onClose={() => setAssociationDelete(null)} onConfirm={() => void removeAssociation()} /> : null}
        {associationModal ? <Modal title={strings.admin.associationAdd} onClose={() => setAssociationModal(false)} actions={<><Button variant="secondary" onClick={() => setAssociationModal(false)}>{strings.modal.cancel}</Button><Button onClick={() => void saveAssociation()} disabled={!associationForm.idEvento || !associationForm.idOpera}>{strings.modal.confirm}</Button></>}>
          <div className="space-y-3">
            <label className="block text-sm text-slate-200">{strings.admin.idEvent}<input type="text" value={associationForm.idEvento} onChange={(event) => setAssociationForm((current) => ({ ...current, idEvento: event.target.value }))} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" /></label>
            <label className="block text-sm text-slate-200">{strings.admin.idWork}<input type="text" value={associationForm.idOpera} onChange={(event) => setAssociationForm((current) => ({ ...current, idOpera: event.target.value }))} className="mt-1 w-full rounded-lg bg-slate-950 px-3 py-2 text-white" /></label>
          </div>
        </Modal> : null}
        {result ? <ResultModal title={result.title} message={result.message} onOk={() => setResult(null)} /> : null}
      </div>
    </ProtectedLayout>
  );
}
