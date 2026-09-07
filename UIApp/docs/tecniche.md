# Specifiche tecniche

## Stack
- React 19
- TanStack Router
- Tailwind CSS
- Fetch browser con wrapper centralizzato

## Convenzioni
- URL API e feature flag arrivano da variabili `VITE_*`.
- In dev, Vite proxy inoltra le richieste API a `http://localhost:8080`.
- La sessione usa cookie client-side e bearer token sulle richieste.
- Le chiamate API passano da `src/lib/apiFetch.ts`.

## Contratti
- Login: `POST /auth/login`
- Utente corrente: `GET /utenti/nome/{nomeUtente}`
- Prenotazioni: `POST /prenotazioni`, `PATCH /prenotazioni/{idPrenotazione}`, `DELETE /prenotazioni/{idPrenotazione}`, `GET /prenotazioni/cercaPrenotazioneByIdUtente/{idUtente}`
- Eventi: `GET /eventi`
- Feedback eventi: `POST /feedback-eventi`, `GET /feedback-eventi/cercaFeedbackEventoByIdUtente/{idUtente}`
