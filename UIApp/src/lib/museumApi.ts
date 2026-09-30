import { apiFetch } from './apiFetch';
import type {
  AuthResponse,
  Evento,
  EventoOpera,
  FeedbackEvento,
  FeedbackOpera,
  Opera,
  Prenotazione,
  PrenotazioniDisponibiliEvento,
  UtenteConRuoli,
  UtenteRuolo,
  UtenteResponse,
} from './types';

export interface BookingCreatePayload {
  idUtente: number;
  idEvento: number;
  numeroPartecipanti: number;
}

export interface BookingUpdatePayload {
  numeroPartecipanti: number;
}

export interface EventCreatePayload {
  nome: string;
  numeroPosti: number;
  data: string;
  durata: number;
}

export interface EventUpdatePayload extends EventCreatePayload {
  idEvento: number;
}

export interface OperaCreatePayload {
  nome: string;
  autore: string;
  tipo: number;
}

export type OperaUpdatePayload = OperaCreatePayload;

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

export interface FeedbackOperaCreatePayload {
  idUtente: number;
  idOpera: number;
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

export interface UserUpdatePayload {
  nomeUtente: string;
  nome: string;
  cognome: string;
  email: string;
  password: string;
}

export async function login(username: string, password: string) {
  const response = await apiFetch<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
  return { ...response, roles: response.roles ?? response.Roles ?? [] };
}

export async function registerUser(payload: RegisterPayload) {
  return apiFetch<void>('/auth/registra', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function updateUser(idUtente: number, payload: UserUpdatePayload) {
  return apiFetch<UtenteResponse>(`/utenti/${idUtente}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  });
}

export async function getUsers() {
  const users = await apiFetch<UtenteResponse[]>('/utenti');
  return Promise.all(users.map((user) => getUserById(user.idUtente)));
}

export async function getUserById(idUtente: number) {
  return apiFetch<UtenteConRuoli>(`/utenti/${idUtente}`);
}

export async function searchUsersByUsername(nomeUtente: string) {
  return apiFetch<UtenteConRuoli[]>(`/utenti/cercaByNomeUtenteLike?nomeUtente=${encodeURIComponent(nomeUtente)}`);
}

export async function getUserByEmail(email: string) {
  return apiFetch<UtenteConRuoli>(`/utenti/email/${encodeURIComponent(email)}`);
}

export async function deleteUser(idUtente: number) {
  return apiFetch<void>(`/utenti/${idUtente}`, { method: 'DELETE' });
}

export async function getAdminUserRole(idUtente: number) {
  return apiFetch<UtenteRuolo>(`/utente-ruoli/cerca?idUtente=${idUtente}&idRuolo=2`);
}

export async function addAdminRole(idUtente: number) {
  return apiFetch<UtenteRuolo>('/utente-ruoli', {
    method: 'POST',
    body: JSON.stringify({ idUtente, idRuolo: 2 }),
  });
}

export async function removeAdminRole(idUtenteRuolo: number) {
  return apiFetch<void>(`/utente-ruoli/${idUtenteRuolo}`, { method: 'DELETE' });
}

export async function getEvents() {
  return apiFetch<Evento[]>('/eventi');
}

export async function getEventsByDate(dataInizio: string, dataFine: string) {
  return apiFetch<Evento[]>(`/eventi/by-data?dataInizio=${encodeURIComponent(dataInizio)}&dataFine=${encodeURIComponent(dataFine)}`);
}

export async function getEventsByName(nomeEvento: string) {
  return apiFetch<Evento[]>(`/eventi/by-nome/${encodeURIComponent(nomeEvento)}`);
}

export async function getEventById(idEvento: number) {
  return apiFetch<Evento>(`/eventi/${idEvento}`);
}

export async function createEvent(payload: EventCreatePayload) {
  return apiFetch<Evento>('/eventi', {
    method: 'POST',
    body: JSON.stringify({ Nome: payload.nome, numeroPosti: payload.numeroPosti, data: payload.data, durata: payload.durata }),
  });
}

export async function updateEvent(idEvento: number, payload: EventUpdatePayload) {
  return apiFetch<Evento>(`/eventi/${idEvento}`, { method: 'PATCH', body: JSON.stringify(payload) });
}

export async function deleteEvent(idEvento: number) {
  return apiFetch<void>(`/eventi/${idEvento}`, { method: 'DELETE' });
}

export async function getBookingsByUserId(idUtente: number) {
  return apiFetch<Prenotazione[]>(`/prenotazioni/cercaPrenotazioneByIdUtente/${idUtente}`);
}

export async function getBookingsByDate(dataInizio: string, dataFine: string) {
  return apiFetch<Prenotazione[]>(`/prenotazioni/by-data?dataInizio=${encodeURIComponent(dataInizio)}&dataFine=${encodeURIComponent(dataFine)}`);
}

export async function getBookingById(idPrenotazione: number) {
  return apiFetch<Prenotazione>(`/prenotazioni/${idPrenotazione}`);
}

export async function getBookingsByEvent(idEvento: number) {
  return apiFetch<Prenotazione[]>(`/eventi/${idEvento}/prenotazioni`);
}

export async function getAvailableBookingsByEvent(idEvento: number) {
  return apiFetch<PrenotazioniDisponibiliEvento>(`/eventi/${idEvento}/prenotazioni/disponibili`);
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

export async function updateFeedbackEvent(idFeedback: number, payload: FeedbackUpdatePayload) {
  return apiFetch<void>(`/feedback-eventi/${idFeedback}`, {
    method: 'PATCH',
    body: JSON.stringify(payload),
  });
}

export async function deleteFeedbackEvent(idFeedback: number) {
  return apiFetch<void>(`/feedback-eventi/${idFeedback}`, {
    method: 'DELETE',
  });
}

export async function getFeedbacksByUserId(idUtente: number) {
  return apiFetch<FeedbackEvento[]>(`/feedback-eventi/cercaFeedbackEventoByIdUtente/${idUtente}`);
}

export async function getOpere() {
  return apiFetch<Opera[]>('/opere');
}

export async function getOpereByNome(nomeOpera: string) {
  return apiFetch<Opera[]>(`/opere/by-nome/${encodeURIComponent(nomeOpera)}`);
}

export async function getOpereByAutore(autoreOpera: string) {
  return apiFetch<Opera[]>(`/opere/by-autore/${encodeURIComponent(autoreOpera)}`);
}

export async function getOperaById(idOpera: number) {
  return apiFetch<Opera>(`/opere/${idOpera}`);
}

export async function createOpera(payload: OperaCreatePayload) {
  return apiFetch<Opera>('/opere', { method: 'POST', body: JSON.stringify(payload) });
}

export async function updateOpera(idOpera: number, payload: OperaUpdatePayload) {
  return apiFetch<Opera>(`/opere/${idOpera}`, { method: 'PATCH', body: JSON.stringify(payload) });
}

export async function deleteOpera(idOpera: number) {
  return apiFetch<void>(`/opere/${idOpera}`, { method: 'DELETE' });
}

export async function getOpereByEvento(idEvento: number) {
  return apiFetch<Opera[]>(`/opere/by-evento/${idEvento}`);
}

export async function getEventoOpereByEvento(idEvento: number) {
  return apiFetch<EventoOpera[]>(`/eventi/${idEvento}/opere`);
}

export async function getEventiByOpera(idOpera: number) {
  return apiFetch<EventoOpera[]>(`/opere/${idOpera}/eventi`);
}

export async function createEventoOpera(payload: EventoOpera) {
  return apiFetch<EventoOpera>('/evento-opera', { method: 'POST', body: JSON.stringify(payload) });
}

export async function deleteEventoOpera(idEvento: number, idOpera: number) {
  return apiFetch<void>(`/evento-opera/${idEvento}/${idOpera}`, { method: 'DELETE' });
}

export async function createFeedbackOpera(payload: FeedbackOperaCreatePayload) {
  return apiFetch<FeedbackOpera>('/feedback-opere', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
}

export async function updateFeedbackOpera(idFeedback: number, payload: FeedbackUpdatePayload) {
  return apiFetch<void>(`/feedback-opere/${idFeedback}`, {
    method: 'PATCH',
    body: JSON.stringify(payload),
  });
}

export async function deleteFeedbackOpera(idFeedback: number) {
  return apiFetch<void>(`/feedback-opere/${idFeedback}`, {
    method: 'DELETE',
  });
}

export async function getFeedbacksOpereByUserId(idUtente: number) {
  return apiFetch<FeedbackOpera[]>(`/feedback-opere/cercaFeedbackOperaByIdUtente/${idUtente}`);
}

export async function getFeedbacksByEventId(idEvento: number) {
  return apiFetch<FeedbackEvento[]>(`/eventi/${idEvento}/feedback-eventi`);
}

export async function getFeedbacksOpereByOperaId(idOpera: number) {
  return apiFetch<FeedbackOpera[]>(`/opere/${idOpera}/feedback-opere`);
}
