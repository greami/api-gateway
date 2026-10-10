package org.api.gateway.application.service;

import org.api.gateway.application.model.GatewayResponse;
import org.api.gateway.application.model.AuthenticatedUser;
import org.api.gateway.application.exception.UnknownUserException;
import org.api.gateway.application.port.in.AuthenticationUseCase;
import org.api.gateway.application.port.in.LoadUserUseCase;
import org.api.gateway.application.port.out.JwtTokenPort;
import org.api.gateway.application.port.out.UserServicePort;
import org.api.gateway.domain.exception.InvalidCredentialsException;
import org.api.gateway.domain.exception.UserNotFoundException;
import org.api.gateway.domain.model.UserAccount;
import org.openapitools.model.AuthRequest;
import org.openapitools.model.AuthResponse;
import org.openapitools.model.RecuperaCredenzialiRequest;
import org.openapitools.model.RegistraRequest;
import org.openapitools.model.RuoloResponse;
import org.openapitools.model.UtenteConRuoliResponse;
import org.openapitools.model.UtenteResponse;

import java.util.List;

public class AuthenticationService implements AuthenticationUseCase, LoadUserUseCase {
    private final UserServicePort userServicePort;
    private final JwtTokenPort jwtTokenPort;

    public AuthenticationService(UserServicePort userServicePort, JwtTokenPort jwtTokenPort) {
        this.userServicePort = userServicePort;
        this.jwtTokenPort = jwtTokenPort;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        GatewayResponse<UtenteResponse> credentials =
                userServicePort.getUtenteByUserNameAndPassword(request.getUsername(), request.getPassword());
        if (credentials.body() == null) {
            throw new InvalidCredentialsException();
        }

        UserAccount user;
        try {
            user = loadAccountByUsername(request.getUsername());
        } catch (UserNotFoundException exception) {
            throw new InvalidCredentialsException();
        }
        if (user.id() == null) {
            throw new IllegalStateException("Authenticated user profile not found");
        }
        return new AuthResponse(
                jwtTokenPort.generateToken(user.username()),
                "Bearer",
                user.username(),
                user.authorities(),
                user.id()
        );
    }

    @Override
    public GatewayResponse<Void> recuperaCredenziali(RecuperaCredenzialiRequest request) {
        return GatewayResponse.of(200, null);
    }

    @Override
    public GatewayResponse<Void> registra(RegistraRequest request) {
        GatewayResponse<UtenteResponse> response = userServicePort.createUtente(request);
        int status = response.statusCode() >= 200 && response.statusCode() < 300
                ? 201
                : response.statusCode();
        return GatewayResponse.of(status, null);
    }

    @Override
    public AuthenticatedUser loadUserByUsername(String username) {
        try {
            UserAccount user = loadAccountByUsername(username);
            return new AuthenticatedUser(user.username(), user.password(), user.authorities());
        } catch (UserNotFoundException exception) {
            throw new UnknownUserException(username, exception);
        }
    }

    private UserAccount loadAccountByUsername(String username) {
        GatewayResponse<UtenteConRuoliResponse> response = userServicePort.getUtenteByNomeUtente(username);
        UtenteConRuoliResponse user = response.body();
        if (user == null) {
            throw new UserNotFoundException(username);
        }

        List<Integer> roleIds = user.getRuoli().stream()
                .map(RuoloResponse::getIdRuolo)
                .toList();
        return new UserAccount(user.getIdUtente(), user.getNomeUtente(), user.getPassword(), roleIds);
    }
}
