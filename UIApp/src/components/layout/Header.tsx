import { useState } from 'react';
import { Link, useNavigate } from '@tanstack/react-router';
import { appConfig } from '../../config/appConfig';
import { strings } from '../../config/strings';
import { useAuth } from '../../state/AuthContext';
import { updateUser } from '../../lib/museumApi';
import { ChangePasswordModal } from '../forms/ChangePasswordModal';
import { ResultModal } from '../forms/ResultModal';

export function Header() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [showPasswordModal, setShowPasswordModal] = useState(false);
  const [result, setResult] = useState<'success' | 'error' | null>(null);

  return (
    <header className="border-b border-white/10 bg-slate-950/80 backdrop-blur">
      <div className="mx-auto flex w-full max-w-6xl items-center justify-between gap-4 px-4 py-4">
        <div>
          <div className="text-lg font-semibold text-white">{appConfig.appName}</div>
          <div className="text-xs text-slate-400">{auth.user?.nomeUtente}</div>
        </div>
        <nav className="flex items-center gap-4 text-sm text-slate-200">
          <Link to="/" className="transition hover:text-cyan-200" activeProps={{ className: 'text-cyan-300' }}>
            {strings.nav.home}
          </Link>
          {appConfig.featureFlags.bookings ? (
            <Link
              to="/prenotazioni"
              className="transition hover:text-cyan-200"
              activeProps={{ className: 'text-cyan-300' }}
            >
              {strings.nav.bookings}
            </Link>
          ) : null}
          {appConfig.featureFlags.feedback ? (
            <Link
              to="/feedback"
              className="transition hover:text-cyan-200"
              activeProps={{ className: 'text-cyan-300' }}
            >
              {strings.nav.feedback}
            </Link>
          ) : null}
          <button
            type="button"
            onClick={() => setShowPasswordModal(true)}
            className="rounded-md border border-white/10 px-3 py-2 text-white transition hover:border-white/20 hover:bg-white/5"
          >
            {strings.nav.changePassword}
          </button>
          <button
            type="button"
            onClick={() => {
              auth.logout();
              void navigate({ to: '/login' });
            }}
            className="rounded-md border border-white/10 px-3 py-2 text-white transition hover:border-white/20 hover:bg-white/5"
          >
            {strings.nav.logout}
          </button>
        </nav>
      </div>
      {showPasswordModal && auth.user ? (
        <ChangePasswordModal
          onClose={() => setShowPasswordModal(false)}
          onConfirm={async (password) => {
            try {
              await updateUser(auth.user!.idUtente, {
                nomeUtente: auth.user!.nomeUtente,
                nome: auth.user!.nome,
                cognome: auth.user!.cognome,
                email: auth.user!.email,
                password,
              });
              setShowPasswordModal(false);
              setResult('success');
            } catch {
              setShowPasswordModal(false);
              setResult('error');
            }
          }}
        />
      ) : null}
      {result ? (
        <ResultModal
          title={result === 'success' ? strings.password.successTitle : strings.password.errorTitle}
          message={result === 'success' ? strings.password.successMessage : strings.password.errorMessage}
          onOk={() => {
            const failed = result === 'error';
            setResult(null);
            if (failed) {
              setShowPasswordModal(true);
            }
          }}
        />
      ) : null}
    </header>
  );
}
