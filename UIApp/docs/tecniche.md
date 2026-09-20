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
- Il login persiste anche `roles`; `ROLE_ADMIN` abilita le pagine amministrative.
- Le chiamate API passano da `src/lib/apiFetch.ts`.

## Contratti
- Login: `POST /auth/login`, restituisce token, username, ruoli e `idUtente`
- Registrazione: `POST /auth/registra`
- Modifica password utente: `PUT /utenti/{idUtente}` con il profilo corrente e la nuova password
- Utente corrente: `GET /utenti/nome/{nomeUtente}`
- Prenotazioni: `CoreApi.yaml` (`POST /prenotazioni`, `PATCH /prenotazioni/{idPrenotazione}`, `DELETE /prenotazioni/{idPrenotazione}`, `GET /prenotazioni/cercaPrenotazioneByIdUtente/{idUtente}`)
- Eventi prenotabili: `GET /eventi/by-data` e ricerca per nome con `GET /eventi/by-nome/{nomeEvento}`; prenotazioni utente con `GET /prenotazioni/cercaPrenotazioneByNomeUtente/{nomeUtente}`
- Feedback eventi: `POST /feedback-eventi`, `GET /feedback-eventi/cercaFeedbackEventoByIdUtente/{idUtente}`
- Opere associate a un evento: `GET /opere/by-evento/{idEvento}`
- Feedback opere: `POST /feedback-opere`, `PATCH /feedback-opere/{idFeedback}`, `DELETE /feedback-opere/{idFeedback}`, `GET /feedback-opere/cercaFeedbackOperaByIdUtente/{idUtente}`
