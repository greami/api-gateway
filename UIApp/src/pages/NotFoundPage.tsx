import { Link } from '@tanstack/react-router';
import { strings } from '../config/strings';

export function NotFoundPage() {
  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="text-center">
        <h1 className="text-2xl font-semibold text-white">{strings.notFound.title}</h1>
        <div className="mt-6">
          <Link to="/" className="inline-flex rounded-lg bg-cyan-500 px-4 py-2 text-sm font-medium text-slate-950">
            {strings.notFound.action}
          </Link>
        </div>
      </div>
    </div>
  );
}
