import type { ReactNode } from 'react';
import { Navigate } from '@tanstack/react-router';
import { strings } from '../../config/strings';
import { useAuth } from '../../state/AuthContext';
import { Header } from './Header';
import { LoadingState } from '../status/LoadingState';

export function ProtectedLayout({ children }: { children: ReactNode }) {
  const auth = useAuth();

  if (auth.status === 'loading') {
    return <LoadingState title={strings.appName} />;
  }

  if (auth.status === 'anonymous') {
    return <Navigate to="/login" />;
  }

  return (
    <div className="min-h-screen">
      <Header />
      <main className="mx-auto w-full max-w-6xl px-4 py-8">{children}</main>
    </div>
  );
}
