package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.EventsUseCase;
import org.openapitools.api.EventiApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class EventiController implements EventiApi {
    private final EventsUseCase useCase;

    public EventiController(EventsUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Void> cancellaEvento(Integer idEvento) {
        return GatewayResponseMapper.toResponseEntity(useCase.cancellaEvento(idEvento));
    }

    @Override
    public ResponseEntity<Evento> cercaEvento(Integer idEvento) {

        return GatewayResponseMapper.toResponseEntity(useCase.cercaEvento(idEvento));
    }

    @Override
    public ResponseEntity<Evento> creaEvento(CreaEventoRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.creaEvento(request));
    }

    @Override
    public ResponseEntity<List<Evento>> eventiAll() {
        return GatewayResponseMapper.toResponseEntity(useCase.eventiAll());
    }

    @Override
    public ResponseEntity<List<Evento>> eventiByData(LocalDateTime dataInizio, LocalDateTime dataFine) {
        return GatewayResponseMapper.toResponseEntity(useCase.eventiByData(dataInizio, dataFine));
    }

    @Override
    public ResponseEntity<List<Evento>> cercaEventoByNome(String nomeEvento) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaEventoByNome(nomeEvento));
    }

    @Override
    public ResponseEntity<Evento> modificaEvento(Integer idEvento, ModificaEventoRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.modificaEvento(idEvento, request));
    }
}
