package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.CoreServiceClient;
import org.openapitools.api.OpereApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OpereController implements OpereApi {
    private final CoreServiceClient client;

    public OpereController(CoreServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<Void> cancellaOpera(Integer idOpera) {
        return client.cancellaOpera(idOpera);
    }

    @Override
    public ResponseEntity<Opera> cercaOperaById(Integer idOpera) {
        return client.cercaOperaById(idOpera);
    }

    @Override
    public ResponseEntity<Opera> creaOpera(CreaOperaRequest request) {
        return client.creaOpera(request);
    }

    @Override
    public ResponseEntity<Void> modificaOpera(Integer idOpera, ModificaOperaRequest request) {
        return client.modificaOpera(idOpera, request);
    }

    @Override
    public ResponseEntity<List<Opera>> opereAll() {
        return client.opereAll();
    }
}
