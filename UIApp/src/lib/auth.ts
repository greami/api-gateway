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

export function getSessionRoles() {
  const roles = getCookie(appConfig.session.rolesCookieName);
  if (!roles) return [];
  try {
    const parsed = JSON.parse(roles);
    return Array.isArray(parsed) && parsed.every((role) => typeof role === 'string') ? parsed : [];
  } catch {
    return [];
  }
}

export function hasRole(role: string) {
  return getSessionRoles().includes(role);
}

export function hasSession() {
  return Boolean(getSessionToken());
}

export function storeSession(token: string, username: string, userId: number, roles: string[]) {
  setCookie(appConfig.session.tokenCookieName, token);
  setCookie(appConfig.session.usernameCookieName, username);
  setCookie(appConfig.session.userIdCookieName, String(userId));
  setCookie(appConfig.session.rolesCookieName, JSON.stringify(roles));
}

export function clearSession() {
  deleteCookie(appConfig.session.tokenCookieName);
  deleteCookie(appConfig.session.usernameCookieName);
  deleteCookie(appConfig.session.userIdCookieName);
  deleteCookie(appConfig.session.rolesCookieName);
}
