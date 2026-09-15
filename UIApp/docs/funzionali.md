# Specifiche funzionali

## Obiettivo
Fornire a un utente standard un gestionale museo per consultare eventi, creare e gestire prenotazioni future e lasciare feedback sugli eventi passati prenotati.

## Flussi
- Login con accesso e registrazione dalla pagina di login.
- Login con sessione persistita in cookie.
- Home con sole prenotazioni future dell’utente.
- Pagina prenotazioni con elenco eventi futuri disponibili, prenotazione, modifica e cancellazione tramite modale.
- Pagina feedback con tab per eventi passati e opere associate agli eventi passati, con invio, modifica e cancellazione del feedback con voto + nota.
- Le opere visualizzate sono esclusivamente quelle restituite da `GET /eventi/{idEvento}/opere` per eventi passati prenotati.

## Regole UI
- Nessun testo statico nei componenti: le stringhe sono centralizzate.
- L’header è visibile solo quando l’utente è autenticato.
- Gli errori 5xx aprono una pagina generica; gli errori 4xx restano inline.
