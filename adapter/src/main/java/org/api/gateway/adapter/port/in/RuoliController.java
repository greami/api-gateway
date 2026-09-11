package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.UserServiceClient;
import org.openapitools.api.RuoliApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RuoliController implements RuoliApi {
    private final UserServiceClient client;

    public RuoliController(UserServiceClient client) {
        this.client = client;
    }

    @Override
    public ResponseEntity<RuoloResponse> createRuolo(RuoloCreateRequest request) {
        return client.createRuolo(request);
    }

    @Override
    public ResponseEntity<Void> deleteRuolo(Integer idRuolo) {
        return client.deleteRuolo(idRuolo);
    }

    @Override
    public ResponseEntity<RuoloResponse> getRuoloById(Integer idRuolo) {
        return client.getRuoloById(idRuolo);
    }

    @Override
    public ResponseEntity<List<RuoloResponse>> listRuoli() {
        return client.listRuoli();
    }

    @Override
    public ResponseEntity<RuoloResponse> updateRuolo(Integer idRuolo, RuoloUpdateRequest request) {
        return client.updateRuolo(idRuolo, request);
    }
}
