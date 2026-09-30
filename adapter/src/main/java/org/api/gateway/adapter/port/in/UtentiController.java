package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.UserServiceClient;
import org.openapitools.api.UtentiApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UtentiController implements UtentiApi {
    private final UserServiceClient client;

    public UtentiController(UserServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<UtenteResponse> createUtente(UtenteCreateRequest request) {
        return client.createUtente(request);
    }

    @Override
    public ResponseEntity<Void> deleteUtente(Integer idUtente) {
        return client.deleteUtente(idUtente);
    }

    @Override
    public ResponseEntity<UtenteConRuoliResponse> getUtenteByEmail(String email) {
        return client.getUtenteByEmail(email);
    }

    @Override
    public ResponseEntity<List<UtenteConRuoliResponse>> cercaUtentiLikeNome(String nomeUtente) {
        return client.cercaUtentiLikeNome(nomeUtente);
    }

    @Override
    public ResponseEntity<UtenteConRuoliResponse> getUtenteById(Integer idUtente) {
        return client.getUtenteById(idUtente);
    }

    @Override
    public ResponseEntity<UtenteConRuoliResponse> getUtenteByNomeUtente(String nomeUtente) {
        return client.getUtenteByNomeUtente(nomeUtente);
    }

    @Override
    public ResponseEntity<UtenteResponse> getUtenteByUserNameAndPassword(String userName, String password) {
        return client.getUtenteByUserNameAndPassword(userName, password);
    }

    @Override
    public ResponseEntity<List<UtenteResponse>> listUtenti() {
        return client.listUtenti();
    }

    @Override
    public ResponseEntity<UtenteResponse> updateUtente(Integer idUtente, UtenteUpdateRequest request) {
        return client.updateUtente(idUtente, request);
    }
}
