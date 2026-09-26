import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';
import { strings } from '../../config/strings';

export function DeleteConfirmModal({
  title,
  message = strings.modal.deleteQuestion,
  confirmLabel = strings.bookings.delete,
  confirmVariant = 'danger',
  busy = false,
  onClose,
  onConfirm,
}: {
  title: string;
  message?: string;
  confirmLabel?: string;
  confirmVariant?: 'primary' | 'secondary' | 'danger';
  busy?: boolean;
  onClose: () => void;
  onConfirm: () => void;
}) {
  return (
    <Modal
      title={title}
      onClose={onClose}
      actions={
        <>
          <Button variant="secondary" type="button" onClick={onClose} disabled={busy}>
            {strings.modal.cancel}
          </Button>
          <Button variant={confirmVariant} type="button" onClick={onConfirm} disabled={busy}>
            {confirmLabel}
          </Button>
        </>
      }
    >
      <p className="whitespace-pre-line text-sm text-slate-300">{message}</p>
    </Modal>
  );
}
