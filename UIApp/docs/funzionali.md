# Specifiche funzionali

## Obiettivo
Fornire un gestionale museo con aree standard e amministrativa, consultazione eventi, gestione prenotazioni e feedback.

## Flussi
- Login con accesso e registrazione dalla pagina di login.
- Login con sessione persistita in cookie.
- Home standard con nome e storia del museo; home amministrativa con collegamenti alla gestione.
- Pagina Eventi e Prenotazioni divisa nei tab Eventi prenotabili e Prenotazioni. Gli eventi sono filtrabili per data fine e nome, ordinabili per data o nome; le prenotazioni future sono modificabili o cancellabili tramite modale.
- Pagina feedback con tab per eventi passati e opere associate agli eventi passati, con invio, modifica e cancellazione del feedback con voto + nota.
- Le opere visualizzate sono esclusivamente quelle restituite da `GET /opere/by-evento/{idEvento}` per eventi passati prenotati.
- Il ruolo `ROLE_ADMIN` abilita le tabelle CRUD di eventi, prenotazioni, opere e feedback; gli altri utenti vedono solo le funzioni standard.

## Regole UI
- Nessun testo statico nei componenti: le stringhe sono centralizzate.
- L’header è visibile solo quando l’utente è autenticato.
- Gli errori 5xx aprono una pagina generica; gli errori 4xx restano inline.
