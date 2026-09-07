import { useState } from 'react';
import { Navigate, useNavigate } from '@tanstack/react-router';
import { strings } from '../config/strings';
import { Button } from '../components/ui/Button';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { useAuth } from '../state/AuthContext';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';

export function LoginPage() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [fatalError, setFatalError] = useState<Error | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (auth.status === 'authenticated') {
    return <Navigate to="/" />;
  }

  if (auth.status === 'loading') {
    return <LoadingState title={strings.appName} />;
  }

  if (fatalError) {
    return <GenericServerErrorPage error={fatalError} />;
  }

  return (
    <div className="mx-auto flex min-h-screen w-full max-w-md items-center px-4">
      <form
        className="w-full rounded-2xl border border-white/10 bg-white/5 p-6 shadow-2xl"
        onSubmit={async (event) => {
          event.preventDefault();
          setSubmitting(true);
          setError(null);
          try {
            await auth.login(username, password);
            await navigate({ to: '/' });
          } catch (caughtError) {
            if (caughtError instanceof ApiError && caughtError.status >= 500) {
              setFatalError(caughtError);
              return;
            }
            setError(caughtError instanceof Error ? caughtError.message : strings.login.inlineErrorPrefix);
          } finally {
            setSubmitting(false);
          }
        }}
      >
        <h1 className="text-2xl font-semibold text-white">{strings.login.title}</h1>
        <p className="mt-2 text-sm text-slate-400">{strings.login.subtitle}</p>

        <div className="mt-6 space-y-4">
          <label className="block text-sm text-slate-200">
            {strings.login.username}
            <input
              value={username}
              onChange={(event) => setUsername(event.target.value)}
              className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
            />
          </label>
          <label className="block text-sm text-slate-200">
            {strings.login.password}
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              className="mt-2 w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-white outline-none focus:border-cyan-400"
            />
          </label>
        </div>

        {error ? <div className="mt-4"><InlineError message={error} /></div> : null}

        <div className="mt-6">
          <Button type="submit" className="w-full" disabled={submitting}>
            {strings.login.submit}
          </Button>
        </div>
      </form>
    </div>
  );
}
