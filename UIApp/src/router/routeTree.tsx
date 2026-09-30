import { Outlet, createRootRoute, createRoute, redirect } from '@tanstack/react-router';
import { LoginPage } from '../pages/LoginPage';
import { HomePage } from '../pages/HomePage';
import { BookingsPage } from '../pages/BookingsPage';
import { FeedbackPage } from '../pages/FeedbackPage';
import { AdminEventsPage } from '../pages/admin/AdminEventsPage';
import { AdminWorksPage } from '../pages/admin/AdminWorksPage';
import { AdminUsersPage } from '../pages/admin/AdminUsersPage';
import { NotFoundPage } from '../pages/NotFoundPage';
import { getSessionToken } from '../lib/auth';

const rootRoute = createRootRoute({
  component: () => <Outlet />,
});

const loginRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/login',
  component: LoginPage,
  beforeLoad: () => {
    if (getSessionToken()) {
      throw redirect({ to: '/' });
    }
  },
});

const homeRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/',
  component: HomePage,
});

const bookingsRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/prenotazioni',
  component: BookingsPage,
});

const eventsRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/eventi',
  component: AdminEventsPage,
});

const worksRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/opere',
  component: AdminWorksPage,
});

const usersRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/utenti',
  component: AdminUsersPage,
});

const feedbackRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '/feedback',
  component: FeedbackPage,
});

const notFoundRoute = createRoute({
  getParentRoute: () => rootRoute,
  path: '*',
  component: NotFoundPage,
});

export const routeTree = rootRoute.addChildren([loginRoute, homeRoute, eventsRoute, bookingsRoute, worksRoute, usersRoute, feedbackRoute, notFoundRoute]);
