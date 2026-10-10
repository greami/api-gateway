package org.api.gateway.application.service;

import org.api.gateway.application.model.GatewayResponse;
import org.api.gateway.application.port.in.RolesUseCase;
import org.api.gateway.application.port.in.UserRolesUseCase;
import org.api.gateway.application.port.in.UsersUseCase;
import org.api.gateway.application.port.out.UserServicePort;
import org.openapitools.model.*;

import java.util.List;

public class UserGatewayService implements UsersUseCase, RolesUseCase, UserRolesUseCase {
    private final UserServicePort userServicePort;

    public UserGatewayService(UserServicePort userServicePort) {
        this.userServicePort = userServicePort;
    }

    @Override
    public GatewayResponse<List<UtenteResponse>> listUtenti() {
        return userServicePort.listUtenti();
    }

    @Override
    public GatewayResponse<List<UtenteConRuoliResponse>> cercaUtentiLikeNome(String nomeUtente) {
        return userServicePort.cercaUtentiLikeNome(nomeUtente);
    }

    @Override
    public GatewayResponse<UtenteResponse> createUtente(UtenteCreateRequest request) {
        return userServicePort.createUtente(request);
    }

    @Override
    public GatewayResponse<UtenteResponse> createUtente(RegistraRequest request) {
        return userServicePort.createUtente(request);
    }

    @Override
    public GatewayResponse<UtenteConRuoliResponse> getUtenteById(Integer idUtente) {
        return userServicePort.getUtenteById(idUtente);
    }

    @Override
    public GatewayResponse<UtenteResponse> updateUtente(Integer idUtente, UtenteUpdateRequest request) {
        return userServicePort.updateUtente(idUtente, request);
    }

    @Override
    public GatewayResponse<Void> deleteUtente(Integer idUtente) {
        return userServicePort.deleteUtente(idUtente);
    }

    @Override
    public GatewayResponse<UtenteConRuoliResponse> getUtenteByEmail(String email) {
        return userServicePort.getUtenteByEmail(email);
    }

    @Override
    public GatewayResponse<UtenteConRuoliResponse> getUtenteByNomeUtente(String nomeUtente) {
        return userServicePort.getUtenteByNomeUtente(nomeUtente);
    }

    @Override
    public GatewayResponse<UtenteResponse> getUtenteByUserNameAndPassword(String userName, String password) {
        return userServicePort.getUtenteByUserNameAndPassword(userName, password);
    }

    @Override
    public GatewayResponse<List<RuoloResponse>> listRuoli() {
        return userServicePort.listRuoli();
    }

    @Override
    public GatewayResponse<RuoloResponse> createRuolo(RuoloCreateRequest request) {
        return userServicePort.createRuolo(request);
    }

    @Override
    public GatewayResponse<RuoloResponse> getRuoloById(Integer idRuolo) {
        return userServicePort.getRuoloById(idRuolo);
    }

    @Override
    public GatewayResponse<RuoloResponse> updateRuolo(Integer idRuolo, RuoloUpdateRequest request) {
        return userServicePort.updateRuolo(idRuolo, request);
    }

    @Override
    public GatewayResponse<Void> deleteRuolo(Integer idRuolo) {
        return userServicePort.deleteRuolo(idRuolo);
    }

    @Override
    public GatewayResponse<List<UtenteRuoloResponse>> listUtenteRuoli() {
        return userServicePort.listUtenteRuoli();
    }

    @Override
    public GatewayResponse<UtenteRuoloResponse> createUtenteRuolo(UtenteRuoloBase request) {
        return userServicePort.createUtenteRuolo(request);
    }

    @Override
    public GatewayResponse<UtenteRuoloResponse> getUtenteRuoloByUtenteAndRuolo(Integer idUtente, Integer idRuolo) {
        return userServicePort.getUtenteRuoloByUtenteAndRuolo(idUtente, idRuolo);
    }

    @Override
    public GatewayResponse<List<Integer>> getRuoliByUtente(Integer idUtente) {
        return userServicePort.getRuoliByUtente(idUtente);
    }

    @Override
    public GatewayResponse<UtenteRuoloResponse> getUtenteRuoloById(Integer idUtenteRuolo) {
        return userServicePort.getUtenteRuoloById(idUtenteRuolo);
    }

    @Override
    public GatewayResponse<UtenteRuoloResponse> updateUtenteRuolo(Integer idUtenteRuolo, UtenteRuoloBase request) {
        return userServicePort.updateUtenteRuolo(idUtenteRuolo, request);
    }

    @Override
    public GatewayResponse<Void> deleteUtenteRuolo(Integer idUtenteRuolo) {
        return userServicePort.deleteUtenteRuolo(idUtenteRuolo);
    }
}
