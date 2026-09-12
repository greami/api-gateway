# Specifiche funzionali

## Obiettivo
Fornire a un utente standard un gestionale museo per consultare eventi, creare e gestire prenotazioni future e lasciare feedback sugli eventi passati prenotati.

## Flussi
- Login con accesso e registrazione dalla pagina di login.
- Login con sessione persistita in cookie.
- Home con sole prenotazioni future dell’utente.
- Pagina prenotazioni con elenco eventi futuri disponibili, prenotazione, modifica e cancellazione tramite modale.
- Pagina feedback con eventi passati prenotati e invio feedback con voto + nota.

## Regole UI
- Nessun testo statico nei componenti: le stringhe sono centralizzate.
- L’header è visibile solo quando l’utente è autenticato.
- Gli errori 5xx aprono una pagina generica; gli errori 4xx restano inline.
