package org.api.gateway.application.port.contract;

import org.api.gateway.application.model.GatewayResponse;
import org.openapitools.model.*;

import java.util.List;

public final class UserOperations {
    private UserOperations() {
    }

    public interface Users {
        GatewayResponse<List<UtenteResponse>> listUtenti();
        GatewayResponse<List<UtenteConRuoliResponse>> cercaUtentiLikeNome(String nomeUtente);
        GatewayResponse<UtenteResponse> createUtente(UtenteCreateRequest request);
        GatewayResponse<UtenteResponse> createUtente(RegistraRequest request);
        GatewayResponse<UtenteConRuoliResponse> getUtenteById(Integer idUtente);
        GatewayResponse<UtenteResponse> updateUtente(Integer idUtente, UtenteUpdateRequest request);
        GatewayResponse<Void> deleteUtente(Integer idUtente);
        GatewayResponse<UtenteConRuoliResponse> getUtenteByEmail(String email);
        GatewayResponse<UtenteConRuoliResponse> getUtenteByNomeUtente(String nomeUtente);
        GatewayResponse<UtenteResponse> getUtenteByUserNameAndPassword(String userName, String password);
    }

    public interface Roles {
        GatewayResponse<List<RuoloResponse>> listRuoli();
        GatewayResponse<RuoloResponse> createRuolo(RuoloCreateRequest request);
        GatewayResponse<RuoloResponse> getRuoloById(Integer idRuolo);
        GatewayResponse<RuoloResponse> updateRuolo(Integer idRuolo, RuoloUpdateRequest request);
        GatewayResponse<Void> deleteRuolo(Integer idRuolo);
    }

    public interface UserRoles {
        GatewayResponse<List<UtenteRuoloResponse>> listUtenteRuoli();
        GatewayResponse<UtenteRuoloResponse> createUtenteRuolo(UtenteRuoloBase request);
        GatewayResponse<UtenteRuoloResponse> getUtenteRuoloByUtenteAndRuolo(Integer idUtente, Integer idRuolo);
        GatewayResponse<List<Integer>> getRuoliByUtente(Integer idUtente);
        GatewayResponse<UtenteRuoloResponse> getUtenteRuoloById(Integer idUtenteRuolo);
        GatewayResponse<UtenteRuoloResponse> updateUtenteRuolo(Integer idUtenteRuolo, UtenteRuoloBase request);
        GatewayResponse<Void> deleteUtenteRuolo(Integer idUtenteRuolo);
    }
}
