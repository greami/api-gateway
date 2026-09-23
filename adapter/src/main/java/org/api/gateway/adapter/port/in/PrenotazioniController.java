package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.CoreServiceClient;
import org.openapitools.api.PrenotazioniApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PrenotazioniController implements PrenotazioniApi {
    private final CoreServiceClient client;

    public PrenotazioniController(CoreServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<Void> cancellaPrenotazione(Integer idPrenotazione) {
        return client.cancellaPrenotazione(idPrenotazione);
    }

    @Override
    public ResponseEntity<Prenotazione> cercaPrenotazioneById(Integer idPrenotazione) {
        return client.cercaPrenotazioneById(idPrenotazione);
    }

    @Override
    public ResponseEntity<List<Prenotazione>> cercaPrenotazioneByIdUtente(Integer idUtente) {
        try {
            return client.cercaPrenotazioneByIdUtente(idUtente);
        } catch (Exception e) {
            // Log the exception if needed
            return ResponseEntity.ok(new ArrayList<>()); // Return 500 Internal Server Error
        }
    }

    @Override
    public ResponseEntity<PrenotazioniDisponibiliEvento> cercaPrenotazioniDisponibiliPerEvento(Integer idEvento) {
        try {
            return client.cercaPrenotazioniDisponibiliPerEvento(idEvento);
        } catch (Exception e) {
            // Log the exception if needed
            PrenotazioniDisponibiliEvento pre = new PrenotazioniDisponibiliEvento();
            pre.setIdEvento(idEvento);
            pre.setNumeroPostiDispobili(0);
            pre.setNumeroPostiTotali(0);
            return ResponseEntity.ok(pre);
        }
    }

    @Override
    public ResponseEntity<List<Prenotazione>> cercaPrenotazioniPerEvento(Integer idEvento) {
        return client.cercaPrenotazioniPerEvento(idEvento);
    }

    @Override
    public ResponseEntity<Prenotazione> creaPrenotazione(CreaPrenotazioneRequest request) {
        return client.creaPrenotazione(request);
    }

    @Override
    public ResponseEntity<List<Prenotazione>> prenotazioniByData(
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        return client.prenotazioniByData(dataInizio, dataFine);
    }

    @Override
    public ResponseEntity<Void> modificaPrenotazione(
            Integer idPrenotazione, ModificaPrenotazioneRequest request) {
        return client.modificaPrenotazione(idPrenotazione, request);
    }
}
