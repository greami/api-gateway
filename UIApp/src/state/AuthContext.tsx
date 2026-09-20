import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { clearSession, getSessionRoles, getSessionToken, getSessionUserId, getSessionUsername, storeSession } from '../lib/auth';
import { login as loginRequest } from '../lib/museumApi';
import type { LoggedUser, SessionState } from '../lib/types';
import { ApiError } from '../lib/apiFetch';

interface AuthContextValue extends SessionState {
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  reload: () => Promise<void>;
}

const AuthContext = createContext<AuthContextValue | null>(null);

async function loadSessionUser() {
  const token = getSessionToken();
  const username = getSessionUsername();

  if (!token || !username) {
    return null;
  }

  return {
    idUtente: getSessionUserId() ?? 0,
    nomeUtente: username,
    nome: '',
    cognome: '',
    email: '',
    token,
    roles: getSessionRoles(),
  };
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [status, setStatus] = useState<SessionState['status']>('loading');
  const [user, setUser] = useState<LoggedUser | null>(null);
  const [error, setError] = useState<Error | null>(null);

  const hydrate = async () => {
    const token = getSessionToken();
    if (!token) {
      setUser(null);
      setStatus('anonymous');
      setError(null);
      return;
    }

    try {
      const sessionUser = await loadSessionUser();
      if (!sessionUser) {
        clearSession();
        setUser(null);
        setStatus('anonymous');
        setError(null);
        return;
      }

      setUser(sessionUser);
      setStatus('authenticated');
      setError(null);
    } catch (caughtError) {
      if (caughtError instanceof ApiError && caughtError.status < 500) {
        clearSession();
        setUser(null);
        setStatus('anonymous');
        setError(null);
        return;
      }

      setError(caughtError instanceof Error ? caughtError : new Error('Session hydration failed'));
      setStatus('error');
    }
  };

  useEffect(() => {
    void hydrate();
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      status,
      user,
      error,
      async login(username: string, password: string) {
        try {
          const auth = await loginRequest(username, password);
          const roles = auth.roles ?? [];
          const sessionUser = {
            idUtente: auth.idUtente,
            nomeUtente: auth.username,
            nome: '',
            cognome: '',
            email: '',
            token: auth.token,
            roles,
          };
          storeSession(auth.token, auth.username, sessionUser.idUtente, roles);
          setUser(sessionUser);
          setStatus('authenticated');
          setError(null);
        } catch (caughtError) {
          clearSession();
          throw caughtError;
        }
      },
      logout() {
        clearSession();
        setUser(null);
        setStatus('anonymous');
        setError(null);
      },
      async reload() {
        await hydrate();
      },
    }),
    [error, status, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
