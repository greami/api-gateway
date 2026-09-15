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
- Registrazione: `POST /auth/registra`
- Modifica password utente: `PUT /utenti/{idUtente}` con il profilo corrente e la nuova password
- Utente corrente: `GET /utenti/nome/{nomeUtente}`
- Prenotazioni: `CoreApi.yaml` (`POST /prenotazioni`, `PATCH /prenotazioni/{idPrenotazione}`, `DELETE /prenotazioni/{idPrenotazione}`, `GET /prenotazioni/cercaPrenotazioneByIdUtente/{idUtente}`)
- Eventi: `GET /eventi`
- Feedback eventi: `POST /feedback-eventi`, `GET /feedback-eventi/cercaFeedbackEventoByIdUtente/{idUtente}`
- Opere associate a un evento: `GET /eventi/{idEvento}/opere`
- Feedback opere: `POST /feedback-opere`, `PATCH /feedback-opere/{idFeedback}`, `DELETE /feedback-opere/{idFeedback}`, `GET /feedback-opere/cercaFeedbackOperaByIdUtente/{idUtente}`
