package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.CoreServiceClient;
import org.openapitools.api.EventoOperaApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EventoOperaController implements EventoOperaApi {
    private final CoreServiceClient client;

    public EventoOperaController(CoreServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<Void> cancellaEventoOpera(Integer idEvento, Integer idOpera) {
        return client.cancellaEventoOpera(idEvento, idOpera);
    }

    @Override
    public ResponseEntity<List<EventoOpera>> cercaEventiPerOpera(Integer idOpera) {
        return client.cercaEventiPerOpera(idOpera);
    }

    @Override
    public ResponseEntity<EventoOpera> cercaEventoOperaById(Integer idEvento, Integer idOpera) {
        return client.cercaEventoOperaById(idEvento, idOpera);
    }

    @Override
    public ResponseEntity<List<EventoOpera>> cercaOperePerEvento(Integer idEvento) {
        return client.cercaOperePerEvento(idEvento);
    }

    @Override
    public ResponseEntity<EventoOpera> creaEventoOpera(CreaEventoOperaRequest request) {
        return client.creaEventoOpera(request);
    }

    @Override
    public ResponseEntity<Void> modificaEventoOpera(
            Integer idEvento, Integer idOpera, ModificaEventoOperaRequest request) {
        return client.modificaEventoOpera(idEvento, idOpera, request);
    }
}
