package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.UserRolesUseCase;
import org.openapitools.api.UtenteRuoliApi;
import org.openapitools.model.UtenteRuoloBase;
import org.openapitools.model.UtenteRuoloResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UtenteRuoliController implements UtenteRuoliApi {
    private final UserRolesUseCase useCase;

    public UtenteRuoliController(UserRolesUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> createUtenteRuolo(UtenteRuoloBase body) {
        return GatewayResponseMapper.toResponseEntity(useCase.createUtenteRuolo(body));
    }

    @Override
    public ResponseEntity<Void> deleteUtenteRuolo(Integer idUtenteRuolo) {
        return GatewayResponseMapper.toResponseEntity(useCase.deleteUtenteRuolo(idUtenteRuolo));
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> getUtenteRuoloById(Integer idUtenteRuolo) {
        return GatewayResponseMapper.toResponseEntity(useCase.getUtenteRuoloById(idUtenteRuolo));
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> getUtenteRuoloByUtenteAndRuolo(Integer idUtente, Integer idRuolo) {
        return GatewayResponseMapper.toResponseEntity(useCase.getUtenteRuoloByUtenteAndRuolo(idUtente, idRuolo));
    }

    @Override
    public ResponseEntity<List<UtenteRuoloResponse>> listUtenteRuoli() {
        return GatewayResponseMapper.toResponseEntity(useCase.listUtenteRuoli());
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> updateUtenteRuolo(Integer idUtenteRuolo, UtenteRuoloBase body) {
        return GatewayResponseMapper.toResponseEntity(useCase.updateUtenteRuolo(idUtenteRuolo, body));
    }

    @GetMapping("/utente-ruoli/utente/{idUtente}")
    public ResponseEntity<List<Integer>> getRuoliByUtente(@PathVariable("idUtente") Integer idUtente) {
        return GatewayResponseMapper.toResponseEntity(useCase.getRuoliByUtente(idUtente));
    }
}
