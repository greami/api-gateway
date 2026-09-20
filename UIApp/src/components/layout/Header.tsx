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
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const [result, setResult] = useState<'success' | 'error' | null>(null);
  const isAdmin = auth.user?.roles.includes('ROLE_ADMIN') ?? false;

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
          {isAdmin ? (
            <>
              <Link to="/eventi" className="transition hover:text-cyan-200" activeProps={{ className: 'text-cyan-300' }}>
                {strings.nav.events}
              </Link>
              <Link to="/prenotazioni" className="transition hover:text-cyan-200" activeProps={{ className: 'text-cyan-300' }}>
                {strings.nav.reservations}
              </Link>
              <Link to="/opere" className="transition hover:text-cyan-200" activeProps={{ className: 'text-cyan-300' }}>
                {strings.nav.works}
              </Link>
            </>
          ) : appConfig.featureFlags.bookings ? (
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
          <div className="relative ml-2">
            <button
              type="button"
              aria-label={strings.nav.profile}
              aria-expanded={showProfileMenu}
              onClick={() => setShowProfileMenu((visible) => !visible)}
              className="flex h-10 w-10 items-center justify-center rounded-full border border-cyan-400/50 bg-cyan-400/10 text-cyan-200 transition hover:bg-cyan-400/20"
            >
              <svg aria-hidden="true" viewBox="0 0 24 24" className="h-5 w-5 fill-current">
                <path d="M12 12a4 4 0 1 0 0-8 4 4 0 0 0 0 8Zm0 2c-4.42 0-8 2.24-8 5v1h16v-1c0-2.76-3.58-5-8-5Z" />
              </svg>
            </button>
            {showProfileMenu ? (
              <div className="absolute right-0 z-40 mt-2 w-48 rounded-xl border border-white/10 bg-slate-900 p-2 shadow-2xl">
                <button
                  type="button"
                  onClick={() => {
                    setShowProfileMenu(false);
                    setShowPasswordModal(true);
                  }}
                  className="block w-full rounded-lg px-3 py-2 text-left text-sm text-slate-200 hover:bg-white/10"
                >
                  {strings.nav.changePassword}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowProfileMenu(false);
                    auth.logout();
                    void navigate({ to: '/login' });
                  }}
                  className="block w-full rounded-lg px-3 py-2 text-left text-sm text-slate-200 hover:bg-white/10"
                >
                  {strings.nav.logout}
                </button>
              </div>
            ) : null}
          </div>
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
