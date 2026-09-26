import type { Opera } from '../../lib/types';
import { strings } from '../../config/strings';
import { Button } from '../ui/Button';

export function OperaCard({
  opera,
  hasFeedback,
  onFeedback,
  onFeedbackDelete,
}: {
  opera: Opera;
  hasFeedback: boolean;
  onFeedback: () => void;
  onFeedbackDelete?: () => void;
}) {
  return (
    <article className="rounded-2xl border border-white/10 bg-white/5 p-5 shadow-lg">
      <div className="flex flex-col gap-4">
        <div>
          <div className="text-xl font-semibold text-white">{opera.nome}</div>
          <div className="mt-1 text-sm text-slate-400">{opera.autore}</div>
          <p className="mt-3 text-sm text-slate-300">
            {hasFeedback ? strings.feedback.alreadyPresent : strings.feedback.available}
          </p>
        </div>
        <div className="flex flex-wrap gap-3">
          <Button onClick={onFeedback}>
            {hasFeedback ? strings.feedback.edit : strings.feedback.submit}
          </Button>
          {hasFeedback && onFeedbackDelete ? (
            <Button variant="danger" onClick={onFeedbackDelete}>
              {strings.feedback.delete}
            </Button>
          ) : null}
        </div>
      </div>
    </article>
  );
}
