import { useMemo, useState } from 'react';
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
  addAdminRole,
  deleteUser,
  getAdminUserRole,
  getUserByEmail,
  getUserById,
  removeAdminRole,
  searchUsersByUsername,
  updateUser,
} from '../../lib/museumApi';
import type { UtenteConRuoli } from '../../lib/types';
import { useAuth } from '../../state/AuthContext';

type SortKey = 'idUtente' | 'nomeUtente' | 'nome' | 'cognome' | 'email' | 'password' | 'ruolo';
type SortDirection = 'asc' | 'desc';
type UserDraft = {
  nomeUtente: string;
  nome: string;
  cognome: string;
  email: string;
  password: string;
};

const emptyDraft: UserDraft = { nomeUtente: '', nome: '', cognome: '', email: '', password: '' };

function UserModal({
  initial,
  onClose,
  onConfirm,
}: {
  initial: UserDraft;
  onClose: () => void;
  onConfirm: (draft: UserDraft) => void;
}) {
  const [draft, setDraft] = useState(initial);
  const fields: [keyof UserDraft, string, string][] = [
    ['nomeUtente', strings.admin.username, 'text'],
    ['nome', strings.admin.name, 'text'],
    ['cognome', strings.admin.surname, 'text'],
    ['email', strings.admin.email, 'email'],
    ['password', strings.admin.password, 'password'],
  ];
  return (
    <Modal
      title={strings.actions.edit}
      onClose={onClose}
      actions={
        <>
          <Button variant="secondary" onClick={onClose}>{strings.modal.cancel}</Button>
          <Button
            onClick={() => onConfirm(draft)}
            disabled={fields.some(([key]) => !draft[key].trim())}
          >
            {strings.modal.save}
          </Button>
        </>
      }
    >
      <div className="space-y-3">
        {fields.map(([key, label, type]) => (
          <label key={key} className="block text-sm text-slate-300">
            {label}
            <input
              type={type}
              value={draft[key]}
              onChange={(event) => setDraft((current) => ({ ...current, [key]: event.target.value }))}
              className="mt-1 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white"
            />
          </label>
        ))}
      </div>
    </Modal>
  );
}

function AdminToggle({ active, onChange }: { active: boolean; onChange: () => void }) {
  return (
    <button
      type="button"
      role="switch"
      aria-checked={active}
      onClick={onChange}
      className={`relative h-6 w-11 rounded-full transition ${active ? 'bg-cyan-500' : 'bg-slate-600'}`}
    >
      <span className={`absolute top-1 h-4 w-4 rounded-full bg-white transition ${active ? 'left-6' : 'left-1'}`} />
    </button>
  );
}

export function AdminUsersPage() {
  const { user } = useAuth();
  const [rows, setRows] = useState<UtenteConRuoli[]>([]);
  const [id, setId] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [sortKey, setSortKey] = useState<SortKey | null>(null);
  const [sortDirection, setSortDirection] = useState<SortDirection | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [editRow, setEditRow] = useState<UtenteConRuoli | null>(null);
  const [deleteRow, setDeleteRow] = useState<UtenteConRuoli | null>(null);
  const [roleRow, setRoleRow] = useState<{ user: UtenteConRuoli; active: boolean } | null>(null);
  const [result, setResult] = useState<{ title: string; message: string } | null>(null);

  const sortedRows = useMemo(() => {
    if (!sortKey || !sortDirection) return rows;
    return [...rows].sort((left, right) => {
      const leftValue = sortKey === 'ruolo' ? (left.ruoli.some((role) => role.idRuolo === 2) ? 1 : 0) : left[sortKey];
      const rightValue = sortKey === 'ruolo' ? (right.ruoli.some((role) => role.idRuolo === 2) ? 1 : 0) : right[sortKey];
      const comparison = typeof leftValue === 'number'
        ? leftValue - (rightValue as number)
        : String(leftValue).localeCompare(String(rightValue), 'it');
      return sortDirection === 'asc' ? comparison : -comparison;
    });
  }, [rows, sortDirection, sortKey]);

  const changeSort = (column: SortKey) => {
    if (sortKey !== column) {
      setSortKey(column);
      setSortDirection('asc');
    } else if (sortDirection === 'asc') {
      setSortDirection('desc');
    } else {
      setSortKey(null);
      setSortDirection(null);
    }
  };

  const load = async () => {
    setLoading(true);
    try {
      let next: UtenteConRuoli[];
      if (id.trim()) {
        next = [await getUserById(Number(id))];
      } else if (username.trim()) {
        next = await searchUsersByUsername(username.trim());
      } else if (email.trim()) {
        next = [await getUserByEmail(email.trim())];
      } else {
        setRows([]);
        setError(null);
        return;
      }
      setRows(next);
      setError(null);
    } catch (caughtError) {
      setRows([]);
      setError(caughtError instanceof Error ? caughtError.message : strings.admin.operationError);
    } finally {
      setLoading(false);
    }
  };

  if (!user?.roles.includes('ROLE_ADMIN')) {
    return <ProtectedLayout><NotFoundPage /></ProtectedLayout>;
  }

  const resetFilters = () => {
    setId('');
    setUsername('');
    setEmail('');
    setRows([]);
    setError(null);
  };

  const save = async (draft: UserDraft) => {
    if (!editRow) return;
    try {
      await updateUser(editRow.idUtente, draft);
      setEditRow(null);
      setResult({ title: strings.admin.usersTitle, message: strings.admin.userSaved });
      await load();
    } catch (caughtError) {
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const remove = async () => {
    if (!deleteRow) return;
    try {
      await deleteUser(deleteRow.idUtente);
      setDeleteRow(null);
      setResult({ title: strings.admin.usersTitle, message: strings.admin.operationSuccess });
      await load();
    } catch (caughtError) {
      setDeleteRow(null);
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const changeRole = async () => {
    if (!roleRow) return;
    try {
      if (roleRow.active) {
        const relation = await getAdminUserRole(roleRow.user.idUtente);
        await removeAdminRole(relation.idUtenteRuolo);
      } else {
        await addAdminRole(roleRow.user.idUtente);
      }
      setRoleRow(null);
      setResult({ title: strings.admin.usersTitle, message: strings.admin.operationSuccess });
      await load();
    } catch (caughtError) {
      setRoleRow(null);
      setResult({ title: strings.admin.operationError, message: caughtError instanceof Error ? caughtError.message : strings.admin.operationError });
    }
  };

  const columns: [string, SortKey][] = [
    [strings.admin.idUser, 'idUtente'],
    [strings.admin.username, 'nomeUtente'],
    [strings.admin.name, 'nome'],
    [strings.admin.surname, 'cognome'],
    [strings.admin.email, 'email'],
    [strings.admin.password, 'password'],
    [strings.admin.role, 'ruolo'],
  ];

  return (
    <ProtectedLayout>
      <div className="space-y-5">
        <h1 className="text-3xl font-semibold text-white">{strings.admin.usersTitle}</h1>
        <div className="grid gap-3 rounded-xl border border-white/10 bg-white/5 p-4 md:grid-cols-4">
          <label className="text-sm text-slate-300">{strings.admin.idUser}<input value={id} disabled={Boolean(username || email)} onChange={(event) => setId(event.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white disabled:opacity-50" /></label>
          <label className="text-sm text-slate-300">{strings.admin.username}<input value={username} disabled={Boolean(id || email)} onChange={(event) => setUsername(event.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white disabled:opacity-50" /></label>
          <label className="text-sm text-slate-300">{strings.admin.email}<input value={email} disabled={Boolean(id || username)} onChange={(event) => setEmail(event.target.value)} className="mt-1 w-full rounded bg-slate-950 px-3 py-2 text-white disabled:opacity-50" /></label>
          <div className="flex items-end gap-2">
            <Button onClick={() => void load()}>{strings.actions.filter}</Button>
            <Button variant="secondary" onClick={resetFilters}>{strings.actions.reset}</Button>
          </div>
        </div>
        {error ? <InlineError message={error} /> : null}
        {loading ? <LoadingState title={strings.appName} /> : null}
        {!loading && sortedRows.length === 0 ? <p className="text-slate-400">{strings.admin.noResults}</p> : null}
        {sortedRows.length > 0 ? (
          <div className="overflow-x-auto rounded-xl border border-white/10">
            <table className="w-full text-left text-sm text-slate-300">
              <thead className="bg-white/10 text-xs text-slate-200">
                <tr>
                  {columns.map(([label, column]) => (
                    <th key={column} className="px-4 py-3">
                      <button type="button" onClick={() => changeSort(column)} className="inline-flex items-center gap-2 hover:text-white">
                        {label}
                        <span aria-hidden="true">{sortKey === column && sortDirection ? (sortDirection === 'asc' ? '↑' : '↓') : '↕'}</span>
                      </button>
                    </th>
                  ))}
                  <th className="px-4 py-3" />
                </tr>
              </thead>
              <tbody>
                {sortedRows.map((row) => {
                  const isAdmin = row.ruoli.some((role) => role.idRuolo === 2);
                  return (
                    <tr key={row.idUtente} className="border-t border-white/10">
                      <td className="px-4 py-3">{row.idUtente}</td>
                      <td className="px-4 py-3">{row.nomeUtente}</td>
                      <td className="px-4 py-3">{row.nome}</td>
                      <td className="px-4 py-3">{row.cognome}</td>
                      <td className="px-4 py-3">{row.email}</td>
                      <td className="px-4 py-3">{row.password}</td>
                      <td className="px-4 py-3"><AdminToggle active={isAdmin} onChange={() => setRoleRow({ user: row, active: isAdmin })} /></td>
                      <td className="px-4 py-3"><div className="flex gap-2"><Button onClick={() => setEditRow(row)}>{strings.actions.edit}</Button><Button variant="danger" onClick={() => setDeleteRow(row)}>{strings.actions.delete}</Button></div></td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        ) : null}
        {editRow ? <UserModal initial={{ nomeUtente: editRow.nomeUtente, nome: editRow.nome, cognome: editRow.cognome, email: editRow.email, password: editRow.password }} onClose={() => setEditRow(null)} onConfirm={(draft) => void save(draft)} /> : null}
        {deleteRow ? <DeleteConfirmModal title={strings.actions.delete} message={strings.admin.deleteUserQuestion} onClose={() => setDeleteRow(null)} onConfirm={() => void remove()} /> : null}
        {roleRow ? <DeleteConfirmModal title={strings.admin.role} confirmLabel={strings.modal.confirm} confirmVariant="primary" message={(roleRow.active ? strings.admin.adminRoleRemoveQuestion : strings.admin.adminRoleAddQuestion).replace('{idUtente}', String(roleRow.user.idUtente)).replace('{nomeUtente}', roleRow.user.nomeUtente)} onClose={() => setRoleRow(null)} onConfirm={() => void changeRole()} /> : null}
        {result ? <ResultModal title={result.title} message={result.message} onOk={() => setResult(null)} /> : null}
      </div>
    </ProtectedLayout>
  );
}
