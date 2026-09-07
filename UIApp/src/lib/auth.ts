import { appConfig } from '../config/appConfig';
import { getCookie, setCookie, deleteCookie } from './cookie';

export function getSessionToken() {
  return getCookie(appConfig.session.tokenCookieName);
}

export function getSessionUsername() {
  return getCookie(appConfig.session.usernameCookieName);
}

export function getSessionUserId() {
  const userId = getCookie(appConfig.session.userIdCookieName);
  return userId ? Number(userId) : null;
}

export function hasSession() {
  return Boolean(getSessionToken());
}

export function storeSession(token: string, username: string, userId: number) {
  setCookie(appConfig.session.tokenCookieName, token);
  setCookie(appConfig.session.usernameCookieName, username);
  setCookie(appConfig.session.userIdCookieName, String(userId));
}

export function clearSession() {
  deleteCookie(appConfig.session.tokenCookieName);
  deleteCookie(appConfig.session.usernameCookieName);
  deleteCookie(appConfig.session.userIdCookieName);
}
