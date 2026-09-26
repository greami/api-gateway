import { useState } from 'react';
import { strings } from '../../config/strings';
import { Button } from '../ui/Button';
import { Modal } from '../ui/Modal';

export function ChangePasswordModal({
  onClose,
  onConfirm,
}: {
  onClose: () => void;
  onConfirm: (password: string) => void;
}) {
  const [password, setPassword] = useState('');

  return (
    <Modal
      title={strings.password.title}
      onClose={onClose}
      actions={
        <>
          <Button type="button" variant="secondary" onClick={onClose}>
            {strings.password.cancel}
          </Button>
          <Button type="button" disabled={!password.trim()} onClick={() => onConfirm(password)}>
            {strings.password.confirm}
          </Button>
        </>
      }
    >
      <label className="block text-sm text-slate-200">
        {strings.password.newPassword}
        <input
          type="password"
          required
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
        />
      </label>
    </Modal>
  );
}
