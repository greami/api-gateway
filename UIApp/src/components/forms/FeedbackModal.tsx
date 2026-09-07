import { useState } from 'react';
import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';
import { strings } from '../../config/strings';

export function FeedbackModal({
  title,
  onClose,
  onConfirm,
}: {
  title: string;
  onClose: () => void;
  onConfirm: (payload: { voto: number; note: string }) => void;
}) {
  const [voto, setVoto] = useState(5);
  const [note, setNote] = useState('');
  const isValid = note.trim().length > 0 && voto >= 1 && voto <= 5;

  return (
    <Modal
      title={title}
      onClose={onClose}
      actions={
        <>
          <Button variant="secondary" type="button" onClick={onClose}>
            {strings.modal.cancel}
          </Button>
          <Button type="button" onClick={() => onConfirm({ voto, note })} disabled={!isValid}>
            {strings.modal.confirm}
          </Button>
        </>
      }
    >
      <div className="space-y-4">
        <label className="block text-sm text-slate-200">
          {strings.modal.rating}
          <input
            type="number"
            min={1}
            max={5}
            required
            value={voto}
            onChange={(event) => setVoto(Number(event.target.value))}
            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
          />
        </label>
        <label className="block text-sm text-slate-200">
          {strings.modal.note}
          <textarea
            value={note}
            onChange={(event) => setNote(event.target.value)}
            rows={4}
            required
            className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
          />
        </label>
      </div>
    </Modal>
  );
}
