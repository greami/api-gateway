package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.RolesUseCase;
import org.openapitools.api.RuoliApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RuoliController implements RuoliApi {
    private final RolesUseCase useCase;

    public RuoliController(RolesUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<RuoloResponse> createRuolo(RuoloCreateRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.createRuolo(request));
    }

    @Override
    public ResponseEntity<Void> deleteRuolo(Integer idRuolo) {
        return GatewayResponseMapper.toResponseEntity(useCase.deleteRuolo(idRuolo));
    }

    @Override
    public ResponseEntity<RuoloResponse> getRuoloById(Integer idRuolo) {
        return GatewayResponseMapper.toResponseEntity(useCase.getRuoloById(idRuolo));
    }

    @Override
    public ResponseEntity<List<RuoloResponse>> listRuoli() {
        return GatewayResponseMapper.toResponseEntity(useCase.listRuoli());
    }

    @Override
    public ResponseEntity<RuoloResponse> updateRuolo(Integer idRuolo, RuoloUpdateRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.updateRuolo(idRuolo, request));
    }
}
