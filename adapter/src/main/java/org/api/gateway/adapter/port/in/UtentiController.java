package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.UsersUseCase;
import org.openapitools.api.UtentiApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UtentiController implements UtentiApi {
    private final UsersUseCase useCase;

    public UtentiController(UsersUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<UtenteResponse> createUtente(UtenteCreateRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.createUtente(request));
    }

    @Override
    public ResponseEntity<Void> deleteUtente(Integer idUtente) {
        return GatewayResponseMapper.toResponseEntity(useCase.deleteUtente(idUtente));
    }

    @Override
    public ResponseEntity<UtenteConRuoliResponse> getUtenteByEmail(String email) {
        return GatewayResponseMapper.toResponseEntity(useCase.getUtenteByEmail(email));
    }

    @Override
    public ResponseEntity<List<UtenteConRuoliResponse>> cercaUtentiLikeNome(String nomeUtente) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaUtentiLikeNome(nomeUtente));
    }

    @Override
    public ResponseEntity<UtenteConRuoliResponse> getUtenteById(Integer idUtente) {
        return GatewayResponseMapper.toResponseEntity(useCase.getUtenteById(idUtente));
    }

    @Override
    public ResponseEntity<UtenteConRuoliResponse> getUtenteByNomeUtente(String nomeUtente) {
        return GatewayResponseMapper.toResponseEntity(useCase.getUtenteByNomeUtente(nomeUtente));
    }

    @Override
    public ResponseEntity<UtenteResponse> getUtenteByUserNameAndPassword(String userName, String password) {
        return GatewayResponseMapper.toResponseEntity(useCase.getUtenteByUserNameAndPassword(userName, password));
    }

    @Override
    public ResponseEntity<List<UtenteResponse>> listUtenti() {
        return GatewayResponseMapper.toResponseEntity(useCase.listUtenti());
    }

    @Override
    public ResponseEntity<UtenteResponse> updateUtente(Integer idUtente, UtenteUpdateRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.updateUtente(idUtente, request));
    }
}
