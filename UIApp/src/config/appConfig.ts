export const appConfig = {
  appName: 'Museo Gestionale',
  museumName: import.meta.env.VITE_MUSEUM_NAME ?? 'Museo Gestionale',
  museumHistory:
    import.meta.env.VITE_MUSEUM_HISTORY ??
    'Un luogo dedicato alla scoperta dell’arte, della cultura e delle storie che hanno attraversato il nostro territorio.',
  apiBaseUrl: import.meta.env.VITE_API_BASE_URL ?? '/',
  featureFlags: {
    bookings: import.meta.env.VITE_FEATURE_BOOKINGS !== 'false',
    feedback: import.meta.env.VITE_FEATURE_FEEDBACK !== 'false',
  },
  session: {
    tokenCookieName: 'uiapp_token',
    usernameCookieName: 'uiapp_username',
    userIdCookieName: 'uiapp_user_id',
    rolesCookieName: 'uiapp_roles',
  },
} as const;
