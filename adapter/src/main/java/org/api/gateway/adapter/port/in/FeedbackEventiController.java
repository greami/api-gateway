package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.EventFeedbackUseCase;
import org.openapitools.api.FeedbackEventiApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FeedbackEventiController implements FeedbackEventiApi {
    private final EventFeedbackUseCase useCase;

    public FeedbackEventiController(EventFeedbackUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Void> cancellaFeedbackEvento(Integer idFeedback) {
        return GatewayResponseMapper.toResponseEntity(useCase.cancellaFeedbackEvento(idFeedback));
    }

    @Override
    public ResponseEntity<List<FeedbackEventi>> cercaFeedbackEventiPerEvento(Integer idEvento) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaFeedbackEventiPerEvento(idEvento));
    }

    @Override
    public ResponseEntity<FeedbackEventi> cercaFeedbackEventoById(Integer idFeedback) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaFeedbackEventoById(idFeedback));
    }

    @Override
    public ResponseEntity<List<FeedbackEventi>> cercaFeedbackEventoByIdUtente(Integer idUtente) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaFeedbackEventoByIdUtente(idUtente));
    }

    @Override
    public ResponseEntity<FeedbackEventi> creaFeedbackEvento(CreaFeedbackEventiRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.creaFeedbackEvento(request));
    }

    @Override
    public ResponseEntity<Void> modificaFeedbackEvento(
            Integer idFeedback, ModificaFeedbackEventiRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.modificaFeedbackEvento(idFeedback, request));
    }
}
