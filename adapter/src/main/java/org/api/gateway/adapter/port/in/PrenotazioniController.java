package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.CoreServiceClient;
import org.openapitools.api.PrenotazioniApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

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
        return client.cercaPrenotazioneByIdUtente(idUtente);
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
    public ResponseEntity<Void> modificaPrenotazione(
            Integer idPrenotazione, ModificaPrenotazioneRequest request) {
        return client.modificaPrenotazione(idPrenotazione, request);
    }
}
