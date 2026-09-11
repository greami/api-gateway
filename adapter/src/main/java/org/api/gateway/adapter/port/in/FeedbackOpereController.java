package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.CoreServiceClient;
import org.openapitools.api.FeedbackOpereApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FeedbackOpereController implements FeedbackOpereApi {
    private final CoreServiceClient client;

    public FeedbackOpereController(CoreServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<Void> cancellaFeedbackOpera(Integer idFeedback) {
        return client.cancellaFeedbackOpera(idFeedback);
    }

    @Override
    public ResponseEntity<FeedbackOpere> cercaFeedbackOperaById(Integer idFeedback) {
        return client.cercaFeedbackOperaById(idFeedback);
    }

    @Override
    public ResponseEntity<List<FeedbackOpere>> cercaFeedbackOperaByIdUtente(Integer idUtente) {
        return client.cercaFeedbackOperaByIdUtente(idUtente);
    }

    @Override
    public ResponseEntity<List<FeedbackOpere>> cercaFeedbackOperePerOpera(Integer idOpera) {
        return client.cercaFeedbackOperePerOpera(idOpera);
    }

    @Override
    public ResponseEntity<FeedbackOpere> creaFeedbackOpera(CreaFeedbackOpereRequest request) {
        return client.creaFeedbackOpera(request);
    }

    @Override
    public ResponseEntity<Void> modificaFeedbackOpera(
            Integer idFeedback, ModificaFeedbackOpereRequest request) {
        return client.modificaFeedbackOpera(idFeedback, request);
    }
}
