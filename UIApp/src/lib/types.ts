export interface AuthResponse {
  token: string;
  tokenType: string;
  username: string;
  idUtente: number;
  roles?: string[];
  Roles?: string[];
}

export interface UtenteResponse {
  idUtente: number;
  nomeUtente: string;
  nome: string;
  cognome: string;
  email: string;
}

export interface Evento {
  idEvento: number;
  nome: string;
  numeroPosti: number;
  data: string;
  durata: number;
}

export interface Prenotazione {
  idPrenotazione: number;
  idUtente: number;
  idEvento: number;
  numeroPartecipanti: number;
  dataCreazione: string;
  dataUltimaModifica: string;
}

export interface FeedbackEvento {
  idFeedback: number;
  idUtente: number;
  idEvento: number;
  voto: number;
  note: string;
}

export interface Opera {
  idOpera: number;
  nome: string;
  autore: string;
  tipo: number;
}

export interface EventoOpera {
  idEvento: number;
  idOpera: number;
}

export interface FeedbackOpera {
  idFeedback: number;
  idUtente: number;
  idOpera: number;
  voto: number;
  note: string;
}

export interface LoggedUser extends UtenteResponse {
  token: string;
  roles: string[];
}

export interface SessionState {
  status: 'loading' | 'anonymous' | 'authenticated' | 'error';
  user: LoggedUser | null;
  error: Error | null;
}
