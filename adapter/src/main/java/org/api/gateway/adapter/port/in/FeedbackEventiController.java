package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.CoreServiceClient;
import org.openapitools.api.FeedbackEventiApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FeedbackEventiController implements FeedbackEventiApi {
    private final CoreServiceClient client;

    public FeedbackEventiController(CoreServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<Void> cancellaFeedbackEvento(Integer idFeedback) {
        return client.cancellaFeedbackEvento(idFeedback);
    }

    @Override
    public ResponseEntity<List<FeedbackEventi>> cercaFeedbackEventiPerEvento(Integer idEvento) {
        return client.cercaFeedbackEventiPerEvento(idEvento);
    }

    @Override
    public ResponseEntity<FeedbackEventi> cercaFeedbackEventoById(Integer idFeedback) {
        return client.cercaFeedbackEventoById(idFeedback);
    }

    @Override
    public ResponseEntity<List<FeedbackEventi>> cercaFeedbackEventoByIdUtente(Integer idUtente) {
        return client.cercaFeedbackEventoByIdUtente(idUtente);
    }

    @Override
    public ResponseEntity<FeedbackEventi> creaFeedbackEvento(CreaFeedbackEventiRequest request) {
        return client.creaFeedbackEvento(request);
    }

    @Override
    public ResponseEntity<Void> modificaFeedbackEvento(
            Integer idFeedback, ModificaFeedbackEventiRequest request) {
        return client.modificaFeedbackEvento(idFeedback, request);
    }
}
