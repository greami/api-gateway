import { Outlet, createRootRoute, createRoute, redirect } from '@tanstack/react-router';
import { LoginPage } from '../pages/LoginPage';
import { HomePage } from '../pages/HomePage';
import { BookingsPage } from '../pages/BookingsPage';
import { FeedbackPage } from '../pages/FeedbackPage';
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

export const routeTree = rootRoute.addChildren([loginRoute, homeRoute, bookingsRoute, feedbackRoute, notFoundRoute]);
