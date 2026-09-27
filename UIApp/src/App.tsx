import { RouterProvider, createRouter } from '@tanstack/react-router';
import { routeTree } from './router/routeTree';
import { AuthProvider, useAuth } from './state/AuthContext';
import { ThemeProvider } from './state/ThemeContext';
import { GenericServerErrorPage } from './pages/GenericServerErrorPage';
import { NotFoundPage } from './pages/NotFoundPage';

const router = createRouter({
  routeTree,
  defaultPreload: 'intent',
  defaultNotFoundComponent: NotFoundPage,
  defaultErrorComponent: ({ error }) => <GenericServerErrorPage error={error instanceof Error ? error : undefined} />,
});

declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router;
  }
}

function RouterWithAuth() {
  const auth = useAuth();

  if (auth.status === 'error') {
    return <GenericServerErrorPage error={auth.error ?? undefined} />;
  }

  return <RouterProvider router={router} />;
}

export function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <RouterWithAuth />
      </AuthProvider>
    </ThemeProvider>
  );
}
