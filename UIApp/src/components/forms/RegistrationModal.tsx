import { useState } from 'react';
import { strings } from '../../config/strings';
import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';
import type { RegisterPayload } from '../../lib/museumApi';

export function RegistrationModal({
  onClose,
  onConfirm,
}: {
  onClose: () => void;
  onConfirm: (payload: RegisterPayload) => void;
}) {
  const [form, setForm] = useState<RegisterPayload>({
    nomeUtente: '',
    nome: '',
    cognome: '',
    email: '',
    password: '',
  });

  const isValid = Boolean(
    form.nomeUtente.trim() &&
      form.nome.trim() &&
      form.cognome.trim() &&
      form.email.trim() &&
      form.password.trim(),
  );

  const updateField = <K extends keyof RegisterPayload>(key: K, value: RegisterPayload[K]) => {
    setForm((current) => ({ ...current, [key]: value }));
  };

  return (
    <Modal
      title={strings.registration.title}
      onClose={onClose}
      actions={
        <>
          <Button variant="secondary" type="button" onClick={onClose}>
            {strings.registration.cancel}
          </Button>
          <Button type="button" disabled={!isValid} onClick={() => onConfirm(form)}>
            {strings.registration.confirm}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        <p className="text-sm text-slate-400">{strings.registration.subtitle}</p>
        <label className="block text-sm text-slate-200">
          {strings.registration.nomeUtente}
          <input
            value={form.nomeUtente}
            onChange={(event) => updateField('nomeUtente', event.target.value)}
            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
          />
        </label>
        <label className="block text-sm text-slate-200">
          {strings.registration.nome}
          <input
            value={form.nome}
            onChange={(event) => updateField('nome', event.target.value)}
            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
          />
        </label>
        <label className="block text-sm text-slate-200">
          {strings.registration.cognome}
          <input
            value={form.cognome}
            onChange={(event) => updateField('cognome', event.target.value)}
            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
          />
        </label>
        <label className="block text-sm text-slate-200">
          {strings.registration.email}
          <input
            type="email"
            value={form.email}
            onChange={(event) => updateField('email', event.target.value)}
            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
          />
        </label>
        <label className="block text-sm text-slate-200">
          {strings.registration.password}
          <input
            type="password"
            value={form.password}
            onChange={(event) => updateField('password', event.target.value)}
            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
          />
        </label>
      </div>
    </Modal>
  );
}
