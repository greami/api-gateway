package org.api.gateway.adapter.port.out;

import feign.FeignException;
import org.api.gateway.adapter.client.UserServiceClient;
import org.api.gateway.application.model.GatewayResponse;
import org.api.gateway.application.port.out.UserServicePort;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public class UserServiceAdapter implements UserServicePort {
    private final UserServiceClient client;

    public UserServiceAdapter(UserServiceClient client) {
        this.client = client;
    }

    @Override
    public GatewayResponse<List<UtenteResponse>> listUtenti() {
        return forward(client::listUtenti);
    }

    @Override
    public GatewayResponse<List<UtenteConRuoliResponse>> cercaUtentiLikeNome(String nomeUtente) {
        return forward(() -> client.cercaUtentiLikeNome(nomeUtente));
    }

    @Override
    public GatewayResponse<UtenteResponse> createUtente(UtenteCreateRequest request) {
        return forward(() -> client.createUtente(request));
    }

    @Override
    public GatewayResponse<UtenteResponse> createUtente(RegistraRequest request) {
        try {
            return forward(() -> client.createUtente(request));
        } catch (FeignException.BadRequest exception) {
            return GatewayResponse.of(400, null);
        }
    }

    @Override
    public GatewayResponse<UtenteConRuoliResponse> getUtenteById(Integer idUtente) {
        return forward(() -> client.getUtenteById(idUtente));
    }

    @Override
    public GatewayResponse<UtenteResponse> updateUtente(Integer idUtente, UtenteUpdateRequest request) {
        return forward(() -> client.updateUtente(idUtente, request));
    }

    @Override
    public GatewayResponse<Void> deleteUtente(Integer idUtente) {
        return forward(() -> client.deleteUtente(idUtente));
    }

    @Override
    public GatewayResponse<UtenteConRuoliResponse> getUtenteByEmail(String email) {
        return forward(() -> client.getUtenteByEmail(email));
    }

    @Override
    public GatewayResponse<UtenteConRuoliResponse> getUtenteByNomeUtente(String nomeUtente) {
        return forward(() -> client.getUtenteByNomeUtente(nomeUtente));
    }

    @Override
    public GatewayResponse<UtenteResponse> getUtenteByUserNameAndPassword(String userName, String password) {
        return forward(() -> client.getUtenteByUserNameAndPassword(userName, password));
    }

    @Override
    public GatewayResponse<List<RuoloResponse>> listRuoli() {
        return forward(client::listRuoli);
    }

    @Override
    public GatewayResponse<RuoloResponse> createRuolo(RuoloCreateRequest request) {
        return forward(() -> client.createRuolo(request));
    }

    @Override
    public GatewayResponse<RuoloResponse> getRuoloById(Integer idRuolo) {
        return forward(() -> client.getRuoloById(idRuolo));
    }

    @Override
    public GatewayResponse<RuoloResponse> updateRuolo(Integer idRuolo, RuoloUpdateRequest request) {
        return forward(() -> client.updateRuolo(idRuolo, request));
    }

    @Override
    public GatewayResponse<Void> deleteRuolo(Integer idRuolo) {
        return forward(() -> client.deleteRuolo(idRuolo));
    }

    @Override
    public GatewayResponse<List<UtenteRuoloResponse>> listUtenteRuoli() {
        return forward(client::listUtenteRuoli);
    }

    @Override
    public GatewayResponse<UtenteRuoloResponse> createUtenteRuolo(UtenteRuoloBase request) {
        return forward(() -> client.createUtenteRuolo(request));
    }

    @Override
    public GatewayResponse<UtenteRuoloResponse> getUtenteRuoloByUtenteAndRuolo(Integer idUtente, Integer idRuolo) {
        return forward(() -> client.getUtenteRuoloByUtenteAndRuolo(idUtente, idRuolo));
    }

    @Override
    public GatewayResponse<List<Integer>> getRuoliByUtente(Integer idUtente) {
        return forward(() -> client.getRuoliByUtente(idUtente));
    }

    @Override
    public GatewayResponse<UtenteRuoloResponse> getUtenteRuoloById(Integer idUtenteRuolo) {
        return forward(() -> client.getUtenteRuoloById(idUtenteRuolo));
    }

    @Override
    public GatewayResponse<UtenteRuoloResponse> updateUtenteRuolo(Integer idUtenteRuolo, UtenteRuoloBase request) {
        return forward(() -> client.updateUtenteRuolo(idUtenteRuolo, request));
    }

    @Override
    public GatewayResponse<Void> deleteUtenteRuolo(Integer idUtenteRuolo) {
        return forward(() -> client.deleteUtenteRuolo(idUtenteRuolo));
    }

    private <T> GatewayResponse<T> forward(Supplier<ResponseEntity<T>> call) {
        return GatewayResponseMapper.from(call.get());
    }
}
