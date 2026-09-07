import { strings } from '../../config/strings';

export function LoadingState({ title }: { title: string }) {
  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="rounded-2xl border border-white/10 bg-white/5 px-6 py-5 text-center text-slate-100 shadow-xl">
        <div className="text-lg font-semibold">{title}</div>
        <div className="mt-2 text-sm text-slate-400">{strings.status.loading}</div>
      </div>
    </div>
  );
}
