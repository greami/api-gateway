import { strings } from '../../config/strings';
import { Button } from '../ui/Button';
import { Modal } from '../ui/Modal';

export function ResultModal({
  title,
  message,
  onOk,
}: {
  title: string;
  message: string;
  onOk: () => void;
}) {
  return (
    <Modal
      title={title}
      onClose={onOk}
      actions={
        <Button type="button" onClick={onOk}>
          {strings.modal.ok}
        </Button>
      }
    >
      <p className="text-sm text-slate-300">{message}</p>
    </Modal>
  );
}
