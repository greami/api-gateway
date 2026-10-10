package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.WorksUseCase;
import org.openapitools.api.OpereApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OpereController implements OpereApi {
    private final WorksUseCase useCase;

    public OpereController(WorksUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Void> cancellaOpera(Integer idOpera) {
        return GatewayResponseMapper.toResponseEntity(useCase.cancellaOpera(idOpera));
    }

    @Override
    public ResponseEntity<Opera> cercaOperaById(Integer idOpera) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaOperaById(idOpera));
    }

    @Override
    public ResponseEntity<Opera> creaOpera(CreaOperaRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.creaOpera(request));
    }

    @Override
    public ResponseEntity<List<Opera>> cercaOpereByIdEvento(Integer idEvento) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaOpereByIdEvento(idEvento));
    }

    @Override
    public ResponseEntity<List<Opera>> cercaOpereByNome(String nomeOpera) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaOpereByNome(nomeOpera));
    }

    @Override
    public ResponseEntity<List<Opera>> cercaOpereByAutore(String autoreOpera) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaOpereByAutore(autoreOpera));
    }

    @Override
    public ResponseEntity<Void> modificaOpera(Integer idOpera, ModificaOperaRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.modificaOpera(idOpera, request));
    }

    @Override
    public ResponseEntity<List<Opera>> opereAll() {
        return GatewayResponseMapper.toResponseEntity(useCase.opereAll());
    }
}
