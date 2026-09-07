import { Modal } from '../ui/Modal';
import { Button } from '../ui/Button';
import { strings } from '../../config/strings';

export function DeleteConfirmModal({
  title,
  onClose,
  onConfirm,
}: {
  title: string;
  onClose: () => void;
  onConfirm: () => void;
}) {
  return (
    <Modal
      title={title}
      onClose={onClose}
      actions={
        <>
          <Button variant="secondary" type="button" onClick={onClose}>
            {strings.modal.cancel}
          </Button>
          <Button variant="danger" type="button" onClick={onConfirm}>
            {strings.bookings.delete}
          </Button>
        </>
      }
    >
      <p className="text-sm text-slate-300">{strings.modal.deleteQuestion}</p>
    </Modal>
  );
}
