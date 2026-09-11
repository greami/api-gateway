package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.CoreServiceClient;
import org.openapitools.api.EventiApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class EventiController implements EventiApi {
    private final CoreServiceClient client;

    public EventiController(CoreServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<Void> cancellaEvento(Integer idEvento) {
        return client.cancellaEvento(idEvento);
    }

    @Override
    public ResponseEntity<Evento> cercaEvento(Integer idEvento) {

        return client.cercaEvento(idEvento);
    }

    @Override
    public ResponseEntity<Evento> creaEvento(CreaEventoRequest request) {
        return client.creaEvento(request);
    }

    @Override
    public ResponseEntity<List<Evento>> eventiAll() {
        return client.eventiAll();
    }

    @Override
    public ResponseEntity<List<Evento>> eventiByData(LocalDateTime dataInizio, LocalDateTime dataFine) {
        return client.eventiByData(dataInizio, dataFine);
    }

    @Override
    public ResponseEntity<Evento> modificaEvento(Integer idEvento, ModificaEventoRequest request) {
        return client.modificaEvento(idEvento, request);
    }
}
