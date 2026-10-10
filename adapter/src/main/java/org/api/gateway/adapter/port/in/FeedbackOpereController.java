package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.WorkFeedbackUseCase;
import org.openapitools.api.FeedbackOpereApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FeedbackOpereController implements FeedbackOpereApi {
    private final WorkFeedbackUseCase useCase;

    public FeedbackOpereController(WorkFeedbackUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Void> cancellaFeedbackOpera(Integer idFeedback) {
        return GatewayResponseMapper.toResponseEntity(useCase.cancellaFeedbackOpera(idFeedback));
    }

    @Override
    public ResponseEntity<FeedbackOpere> cercaFeedbackOperaById(Integer idFeedback) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaFeedbackOperaById(idFeedback));
    }

    @Override
    public ResponseEntity<List<FeedbackOpere>> cercaFeedbackOperaByIdUtente(Integer idUtente) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaFeedbackOperaByIdUtente(idUtente));
    }

    @Override
    public ResponseEntity<List<FeedbackOpere>> cercaFeedbackOperePerOpera(Integer idOpera) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaFeedbackOperePerOpera(idOpera));
    }

    @Override
    public ResponseEntity<FeedbackOpere> creaFeedbackOpera(CreaFeedbackOpereRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.creaFeedbackOpera(request));
    }

    @Override
    public ResponseEntity<Void> modificaFeedbackOpera(
            Integer idFeedback, ModificaFeedbackOpereRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.modificaFeedbackOpera(idFeedback, request));
    }
}
