import { apiFetch } from './apiFetch';
import type { AuthResponse, Evento, FeedbackEvento, Prenotazione, UtenteResponse } from './types';

export interface BookingCreatePayload {
  idUtente: number;
  idEvento: number;
  numeroPartecipanti: number;
}

export interface BookingUpdatePayload {
  numeroPartecipanti: number;
}

export interface FeedbackCreatePayload {
  idUtente: number;
  idEvento: number;
  voto: number;
  note: string;
}

export interface FeedbackUpdatePayload {
  voto: number;
  note: string;
}

export interface RegisterPayload {
  nomeUtente: string;
  nome: string;
  cognome: string;
  email: string;
  password: string;
}

export async function login(username: string, password: string) {
  return apiFetch<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export async function registerUser(payload: RegisterPayload) {
  return apiFetch<void>('http://localhost:8080/auth/registra', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function getUserByUsername(username: string) {
  return apiFetch<UtenteResponse>(`/utenti/nome/${encodeURIComponent(username)}`);
}

export async function getEvents() {
  return apiFetch<Evento[]>('/eventi');
}

export async function getBookingsByUserId(idUtente: number) {
  return apiFetch<Prenotazione[]>(`/prenotazioni/cercaPrenotazioneByIdUtente/${idUtente}`);
}

export async function createBooking(payload: BookingCreatePayload) {
  return apiFetch<Prenotazione>('/prenotazioni', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function updateBooking(idPrenotazione: number, payload: BookingUpdatePayload) {
  return apiFetch<Prenotazione>(`/prenotazioni/${idPrenotazione}`, {
    method: 'PATCH',
    body: JSON.stringify(payload),
  });
}

export async function deleteBooking(idPrenotazione: number) {
  return apiFetch<void>(`/prenotazioni/${idPrenotazione}`, {
    method: 'DELETE',
  });
}

export async function createFeedbackEvent(payload: FeedbackCreatePayload) {
  return apiFetch<FeedbackEvento>('/feedback-eventi', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function getFeedbacksByUserId(idUtente: number) {
  return apiFetch<FeedbackEvento[]>(`/feedback-eventi/cercaFeedbackEventoByIdUtente/${idUtente}`);
}
