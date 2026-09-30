import { Link } from '@tanstack/react-router';
import { ProtectedLayout } from '../components/layout/ProtectedLayout';
import { strings } from '../config/strings';
import { appConfig } from '../config/appConfig';
import { useAuth } from '../state/AuthContext';

export function HomePage() {
  const { user } = useAuth();
  const isAdmin = user?.roles.includes('ROLE_ADMIN') ?? false;

  return (
    <ProtectedLayout>
      {isAdmin ? (
        <div className="space-y-8">
          <section className="rounded-2xl border border-white/10 bg-white/5 p-8 text-center">
            <h1 className="text-3xl font-semibold text-white">{appConfig.museumName}</h1>
            <p className="mt-3 text-slate-300">{strings.home.adminTitle}</p>
          </section>
          <section>
            <h2 className="mb-4 text-xl font-semibold text-white">{strings.home.adminLinks}</h2>
            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              {[
                ['/eventi', strings.nav.events],
                ['/prenotazioni', strings.nav.reservations],
                ['/opere', strings.nav.works],
                ['/feedback', strings.nav.feedback],
                ['/utenti', strings.nav.users],
              ].map(([to, label]) => (
                <Link
                  key={to}
                  to={to as '/eventi'}
                  className="rounded-xl border border-white/10 bg-white/5 p-5 text-center font-medium text-cyan-200 transition hover:bg-white/10"
                >
                  {label}
                </Link>
              ))}
            </div>
          </section>
        </div>
      ) : (
        <div className="mx-auto max-w-3xl space-y-8 text-center">
          <section className="rounded-2xl border border-white/10 bg-white/5 p-10 shadow-xl">
            <h1 className="text-4xl font-semibold text-white">{appConfig.museumName}</h1>
            <p className="mt-6 text-left text-base leading-7 text-slate-300">{appConfig.museumHistory}</p>
          </section>
        </div>
      )}
    </ProtectedLayout>
  );
}
