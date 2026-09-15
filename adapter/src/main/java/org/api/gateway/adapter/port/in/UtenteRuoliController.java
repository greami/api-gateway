package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.UserServiceClient;
import org.openapitools.api.UtenteRuoliApi;
import org.openapitools.model.UtenteRuoloBase;
import org.openapitools.model.UtenteRuoloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UtenteRuoliController implements UtenteRuoliApi {
    private final UserServiceClient client;

    public UtenteRuoliController(UserServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> createUtenteRuolo(UtenteRuoloBase body) {
        return client.createUtenteRuolo(body);
    }

    @Override
    public ResponseEntity<Void> deleteUtenteRuolo(Integer idUtenteRuolo) {
        return client.deleteUtenteRuolo(idUtenteRuolo);
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> getUtenteRuoloById(Integer idUtenteRuolo) {
        return client.getUtenteRuoloById(idUtenteRuolo);
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> getUtenteRuoloByUtenteAndRuolo(Integer idUtente, Integer idRuolo) {
        return client.getUtenteRuoloByUtenteAndRuolo(idUtente, idRuolo);
    }

    @Override
    public ResponseEntity<List<UtenteRuoloResponse>> listUtenteRuoli() {
        return client.listUtenteRuoli();
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> updateUtenteRuolo(Integer idUtenteRuolo, UtenteRuoloBase body) {
        return client.updateUtenteRuolo(idUtenteRuolo, body);
    }

    @GetMapping("/utente-ruoli/utente/{idUtente}")
    public ResponseEntity<List<Integer>> getRuoliByUtente(@PathVariable("idUtente") Integer idUtente) {
        return client.getRuoliByUtente(idUtente);
    }
}
