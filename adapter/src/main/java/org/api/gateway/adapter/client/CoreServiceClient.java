package org.api.gateway.adapter.client;

import org.openapitools.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "core-service", url = "${services.core-service.base-url}")
public interface CoreServiceClient {

    @DeleteMapping("/eventi/{idEvento}")
    ResponseEntity<Void> cancellaEvento(@PathVariable("idEvento") Integer idEvento);

    @GetMapping("/eventi/{idEvento}")
    ResponseEntity<Evento> cercaEvento(@PathVariable("idEvento") Integer idEvento);

    @PostMapping("/eventi")
    ResponseEntity<Evento> creaEvento(@RequestBody CreaEventoRequest request);

    @GetMapping("/eventi")
    ResponseEntity<List<Evento>> eventiAll();

    @GetMapping("/eventi/by-data")
    ResponseEntity<List<Evento>> eventiByData(
            @RequestParam("dataInizio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInizio,
            @RequestParam("dataFine") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFine);

    @GetMapping("/eventi/by-nome/{nomeEvento}")
    ResponseEntity<List<Evento>> cercaEventoByNome(
            @PathVariable("nomeEvento") String nomeEvento);

    @PatchMapping("/eventi/{idEvento}")
    ResponseEntity<Evento> modificaEvento(
            @PathVariable("idEvento") Integer idEvento,
            @RequestBody ModificaEventoRequest request);

    @DeleteMapping("/opere/{idOpera}")
    ResponseEntity<Void> cancellaOpera(@PathVariable("idOpera") Integer idOpera);

    @GetMapping("/opere/{idOpera}")
    ResponseEntity<Opera> cercaOperaById(@PathVariable("idOpera") Integer idOpera);

    @PostMapping("/opere")
    ResponseEntity<Opera> creaOpera(@RequestBody CreaOperaRequest request);

    @PatchMapping("/opere/{idOpera}")
    ResponseEntity<Void> modificaOpera(
            @PathVariable("idOpera") Integer idOpera,
            @RequestBody ModificaOperaRequest request);

    @GetMapping("/opere")
    ResponseEntity<List<Opera>> opereAll();

    @GetMapping("/opere/by-evento/{idEvento}")
    ResponseEntity<List<Opera>> cercaOpereByIdEvento(
            @PathVariable("idEvento") Integer idEvento);

    @GetMapping("/opere/by-nome/{nomeOpera}")
    ResponseEntity<List<Opera>> cercaOpereByNome(
            @PathVariable("nomeOpera") String nomeOpera);

    @GetMapping("/opere/by-autore/{autoreOpera}")
    ResponseEntity<List<Opera>> cercaOpereByAutore(
            @PathVariable("autoreOpera") String autoreOpera);

    @DeleteMapping("/evento-opera/{idEvento}/{idOpera}")
    ResponseEntity<Void> cancellaEventoOpera(
            @PathVariable("idEvento") Integer idEvento,
            @PathVariable("idOpera") Integer idOpera);

    @GetMapping("/opere/{idOpera}/eventi")
    ResponseEntity<List<EventoOpera>> cercaEventiPerOpera(@PathVariable("idOpera") Integer idOpera);

    @GetMapping("/evento-opera/{idEvento}/{idOpera}")
    ResponseEntity<EventoOpera> cercaEventoOperaById(
            @PathVariable("idEvento") Integer idEvento,
            @PathVariable("idOpera") Integer idOpera);

    @GetMapping("/eventi/{idEvento}/opere")
    ResponseEntity<List<EventoOpera>> cercaOperePerEvento(@PathVariable("idEvento") Integer idEvento);

    @PostMapping("/evento-opera")
    ResponseEntity<EventoOpera> creaEventoOpera(@RequestBody CreaEventoOperaRequest request);

    @PatchMapping("/evento-opera/{idEvento}/{idOpera}")
    ResponseEntity<Void> modificaEventoOpera(
            @PathVariable("idEvento") Integer idEvento,
            @PathVariable("idOpera") Integer idOpera,
            @RequestBody ModificaEventoOperaRequest request);

    @DeleteMapping("/feedback-eventi/{idFeedback}")
    ResponseEntity<Void> cancellaFeedbackEvento(@PathVariable("idFeedback") Integer idFeedback);

    @GetMapping("/eventi/{idEvento}/feedback-eventi")
    ResponseEntity<List<FeedbackEventi>> cercaFeedbackEventiPerEvento(
            @PathVariable("idEvento") Integer idEvento);

    @GetMapping("/feedback-eventi/{idFeedback}")
    ResponseEntity<FeedbackEventi> cercaFeedbackEventoById(@PathVariable("idFeedback") Integer idFeedback);

    @GetMapping("/feedback-eventi/cercaFeedbackEventoByIdUtente/{idUtente}")
    ResponseEntity<List<FeedbackEventi>> cercaFeedbackEventoByIdUtente(@PathVariable("idUtente") Integer idUtente);

    @PostMapping("/feedback-eventi")
    ResponseEntity<FeedbackEventi> creaFeedbackEvento(@RequestBody CreaFeedbackEventiRequest request);

    @PatchMapping("/feedback-eventi/{idFeedback}")
    ResponseEntity<Void> modificaFeedbackEvento(
            @PathVariable("idFeedback") Integer idFeedback,
            @RequestBody ModificaFeedbackEventiRequest request);

    @DeleteMapping("/feedback-opere/{idFeedback}")
    ResponseEntity<Void> cancellaFeedbackOpera(@PathVariable("idFeedback") Integer idFeedback);

    @GetMapping("/feedback-opere/{idFeedback}")
    ResponseEntity<FeedbackOpere> cercaFeedbackOperaById(@PathVariable("idFeedback") Integer idFeedback);

    @GetMapping("/feedback-opere/cercaFeedbackOperaByIdUtente/{idUtente}")
    ResponseEntity<List<FeedbackOpere>> cercaFeedbackOperaByIdUtente(@PathVariable("idUtente") Integer idUtente);

    @GetMapping("/opere/{idOpera}/feedback-opere")
    ResponseEntity<List<FeedbackOpere>> cercaFeedbackOperePerOpera(@PathVariable("idOpera") Integer idOpera);

    @PostMapping("/feedback-opere")
    ResponseEntity<FeedbackOpere> creaFeedbackOpera(@RequestBody CreaFeedbackOpereRequest request);

    @PatchMapping("/feedback-opere/{idFeedback}")
    ResponseEntity<Void> modificaFeedbackOpera(
            @PathVariable("idFeedback") Integer idFeedback,
            @RequestBody ModificaFeedbackOpereRequest request);

    @DeleteMapping("/prenotazioni/{idPrenotazione}")
    ResponseEntity<Void> cancellaPrenotazione(@PathVariable("idPrenotazione") Integer idPrenotazione);

    @GetMapping("/prenotazioni/{idPrenotazione}")
    ResponseEntity<Prenotazione> cercaPrenotazioneById(@PathVariable("idPrenotazione") Integer idPrenotazione);

    @GetMapping("/prenotazioni/cercaPrenotazioneByIdUtente/{idUtente}")
    ResponseEntity<List<Prenotazione>> cercaPrenotazioneByIdUtente(@PathVariable("idUtente") Integer idUtente);

    @GetMapping("/eventi/{idEvento}/prenotazioni")
    ResponseEntity<List<Prenotazione>> cercaPrenotazioniPerEvento(@PathVariable("idEvento") Integer idEvento);

    @GetMapping("/eventi/{idEvento}/prenotazioni/disponibili")
    ResponseEntity<PrenotazioniDisponibiliEvento> cercaPrenotazioniDisponibiliPerEvento(
            @PathVariable("idEvento") Integer idEvento);

    @PostMapping("/prenotazioni")
    ResponseEntity<Prenotazione> creaPrenotazione(@RequestBody CreaPrenotazioneRequest request);

    @GetMapping("/prenotazioni/by-data")
    ResponseEntity<List<Prenotazione>> prenotazioniByData(
            @RequestParam("dataInizio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataInizio,
            @RequestParam("dataFine") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataFine);

    @PatchMapping("/prenotazioni/{idPrenotazione}")
    ResponseEntity<Void> modificaPrenotazione(
            @PathVariable("idPrenotazione") Integer idPrenotazione,
            @RequestBody ModificaPrenotazioneRequest request);
}
