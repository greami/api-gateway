package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.EventWorksUseCase;
import org.openapitools.api.EventoOperaApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventoOperaController implements EventoOperaApi {
    private final EventWorksUseCase useCase;

    public EventoOperaController(EventWorksUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Void> cancellaEventoOpera(Integer idEvento, Integer idOpera) {
        return GatewayResponseMapper.toResponseEntity(useCase.cancellaEventoOpera(idEvento, idOpera));
    }

    @Override
    public ResponseEntity<List<EventoOpera>> cercaEventiPerOpera(Integer idOpera) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaEventiPerOpera(idOpera));
    }

    @Override
    public ResponseEntity<EventoOpera> cercaEventoOperaById(Integer idEvento, Integer idOpera) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaEventoOperaById(idEvento, idOpera));
    }

    @Override
    public ResponseEntity<List<EventoOpera>> cercaOperePerEvento(Integer idEvento) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaOperePerEvento(idEvento));
    }

    @Override
    public ResponseEntity<EventoOpera> creaEventoOpera(CreaEventoOperaRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.creaEventoOpera(request));
    }

    @Override
    public ResponseEntity<Void> modificaEventoOpera(
            Integer idEvento, Integer idOpera, ModificaEventoOperaRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.modificaEventoOpera(idEvento, idOpera, request));
    }
}
