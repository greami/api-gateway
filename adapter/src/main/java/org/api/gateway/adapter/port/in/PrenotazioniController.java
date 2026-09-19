package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.CoreServiceClient;
import org.api.gateway.adapter.client.UserServiceClient;
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
    private final UserServiceClient userServiceClient;

    public PrenotazioniController(CoreServiceClient client, UserServiceClient userServiceClient) {
        this.client = client;
        this.userServiceClient = userServiceClient;
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
    public ResponseEntity<List<Prenotazione>> cercaPrenotazioneByNomeUtente(String nomeUtente) {

        ResponseEntity<UtenteResponse> userResponse;
        try{
            userResponse = userServiceClient.getUtenteByNomeUtente(nomeUtente);
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        if(userResponse!= null && userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null)
        {
            try {
                return client.cercaPrenotazioneByIdUtente(userResponse.getBody().getIdUtente());
            } catch (Exception e) {
                return ResponseEntity.ok(new ArrayList<>());
            }
        }

        return ResponseEntity.ok(new ArrayList<>());
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
