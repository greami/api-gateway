import { useState } from 'react';
import { Navigate, useNavigate } from '@tanstack/react-router';
import { strings } from '../config/strings';
import { Button } from '../components/ui/Button';
import { InlineError } from '../components/status/InlineError';
import { LoadingState } from '../components/status/LoadingState';
import { useAuth } from '../state/AuthContext';
import { ApiError } from '../lib/apiFetch';
import { GenericServerErrorPage } from './GenericServerErrorPage';
import { RegistrationModal } from '../components/forms/RegistrationModal';
import { registerUser } from '../lib/museumApi';

export function LoginPage() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [registrationSuccess, setRegistrationSuccess] = useState(false);
  const [registrationError, setRegistrationError] = useState(false);
  const [fatalError, setFatalError] = useState<Error | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [showRegistration, setShowRegistration] = useState(false);

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

        <div className="mt-6 space-y-3">
          <Button type="submit" className="w-full" disabled={submitting}>
            {strings.login.submit}
          </Button>
          <Button type="button" variant="secondary" className="w-full" onClick={() => setShowRegistration(true)}>
            {strings.login.register}
          </Button>
        </div>
      </form>

      {showRegistration ? (
        <RegistrationModal
          onClose={async () => {
            setRegistrationError(false);
            setRegistrationSuccess(false);
            setShowRegistration(false);
            await navigate({ to: '/login' });
          }}
          onConfirm={async (payload) => {
            setError(null);
            setRegistrationError(false);
            try {
              await registerUser(payload);
              setShowRegistration(false);
              setRegistrationSuccess(true);
            } catch (caughtError) {
              if (caughtError instanceof ApiError && caughtError.status >= 500) {
                setFatalError(caughtError);
                return;
              }
              setRegistrationError(true);
            }
          }}
        />
      ) : null}

      {registrationSuccess ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/80 p-4">
          <div className="w-full max-w-lg rounded-2xl border border-white/10 bg-slate-900 p-6 shadow-2xl">
            <h2 className="text-lg font-semibold text-white">{strings.registration.successTitle}</h2>
            <div className="mt-6 flex justify-end">
              <Button
                type="button"
                onClick={async () => {
                  setRegistrationSuccess(false);
                  await navigate({ to: '/login' });
                }}
              >
                {strings.registration.successOk}
              </Button>
            </div>
          </div>
        </div>
      ) : null}

      {registrationError ? (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/80 p-4">
          <div className="w-full max-w-lg rounded-2xl border border-white/10 bg-slate-900 p-6 shadow-2xl">
            <h2 className="text-lg font-semibold text-white">{strings.registration.errorTitle}</h2>
            <p className="mt-4 text-sm text-slate-300">{strings.registration.errorMessage}</p>
            <div className="mt-6 flex justify-end">
              <Button
                type="button"
                onClick={() => {
                  setRegistrationError(false);
                  setShowRegistration(true);
                }}
              >
                {strings.registration.errorOk}
              </Button>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}
