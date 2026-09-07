import { useState } from 'react';
import { strings } from '../../config/strings';
import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';

export function BookingModal({
  title,
  initialParticipants = 2,
  confirmLabel,
  onClose,
  onConfirm,
}: {
  title: string;
  initialParticipants?: number;
  confirmLabel: string;
  onClose: () => void;
  onConfirm: (participants: number) => void;
}) {
  const [participants, setParticipants] = useState(initialParticipants);
  const isValid = participants >= 2;

  return (
    <Modal
      title={title}
      onClose={onClose}
      actions={
        <>
          <Button variant="secondary" type="button" onClick={onClose}>
            {strings.modal.cancel}
          </Button>
          <Button type="button" onClick={() => onConfirm(participants)} disabled={!isValid}>
            {confirmLabel}
          </Button>
        </>
      }
    >
      <label className="block text-sm text-slate-200">
        {strings.modal.participants}
        <input
          type="number"
          min={2}
          required
          value={participants}
          onChange={(event) => setParticipants(Number(event.target.value))}
          className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none ring-0 focus:border-cyan-400"
        />
      </label>
    </Modal>
  );
}
