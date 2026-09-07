export const appConfig = {
  appName: 'Museo Gestionale',
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL ?? '/',
  featureFlags: {
    bookings: import.meta.env.VITE_FEATURE_BOOKINGS !== 'false',
    feedback: import.meta.env.VITE_FEATURE_FEEDBACK !== 'false',
  },
  session: {
    tokenCookieName: 'uiapp_token',
    usernameCookieName: 'uiapp_username',
    userIdCookieName: 'uiapp_user_id',
  },
} as const;
