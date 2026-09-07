import { strings } from '../config/strings';
import { Button } from '../components/ui/Button';

export function GenericServerErrorPage({ error }: { error?: Error }) {
  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="max-w-xl rounded-2xl border border-white/10 bg-white/5 p-8 text-center shadow-2xl">
        <h1 className="text-2xl font-semibold text-white">{strings.errors.genericServerTitle}</h1>
        <p className="mt-3 text-slate-300">{strings.errors.genericServerMessage}</p>
        {error ? <p className="mt-4 text-sm text-slate-400">{error.message}</p> : null}
        <div className="mt-6">
          <Button type="button" onClick={() => window.location.assign('/')}>
            {strings.actions.backHome}
          </Button>
        </div>
      </div>
    </div>
  );
}
