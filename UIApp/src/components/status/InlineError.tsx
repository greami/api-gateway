import { strings } from '../../config/strings';

export function InlineError({ message }: { message: string }) {
  return (
    <div className="rounded-xl border border-rose-500/40 bg-rose-500/10 px-4 py-3 text-sm text-rose-200">
      <strong>{strings.errors.inlinePrefix}</strong> {message}
    </div>
  );
}
