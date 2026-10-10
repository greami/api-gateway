package org.api.gateway.application.service;

import org.api.gateway.application.model.GatewayResponse;
import org.api.gateway.application.port.out.JwtTokenPort;
import org.api.gateway.application.port.out.UserServicePort;
import org.api.gateway.domain.exception.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.AuthRequest;
import org.openapitools.model.AuthResponse;
import org.openapitools.model.RegistraRequest;
import org.openapitools.model.RuoloResponse;
import org.openapitools.model.UtenteConRuoliResponse;
import org.openapitools.model.UtenteResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private UserServicePort userServicePort;

    @Mock
    private JwtTokenPort jwtTokenPort;

    @Test
    void authenticatesUserAndCreatesTokenUsingDomainAuthorities() {
        when(userServicePort.getUtenteByUserNameAndPassword("admin", "secret"))
                .thenReturn(GatewayResponse.of(200, user()));
        when(userServicePort.getUtenteByNomeUtente("admin"))
                .thenReturn(GatewayResponse.of(200, profile()));
        when(jwtTokenPort.generateToken("admin")).thenReturn("signed-token");

        AuthResponse response = new AuthenticationService(userServicePort, jwtTokenPort)
                .login(new AuthRequest("admin", "secret"));

        assertEquals("signed-token", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("admin", response.getUsername());
        assertEquals(List.of("ROLE_ADMIN"), response.getRoles());
        assertEquals(1000, response.getIdUtente());
        verify(jwtTokenPort).generateToken("admin");
    }

    @Test
    void rejectsCredentialsWhenTheUserServiceReturnsNoUser() {
        when(userServicePort.getUtenteByUserNameAndPassword("user01", "wrong"))
                .thenReturn(GatewayResponse.<UtenteResponse>of(401, null));

        AuthenticationService service = new AuthenticationService(userServicePort, jwtTokenPort);

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new AuthRequest("user01", "wrong")));
    }

    @Test
    void preservesRegistrationStatusMapping() {
        when(userServicePort.createUtente(org.mockito.ArgumentMatchers.any(RegistraRequest.class)))
                .thenReturn(GatewayResponse.of(200, user()));
        AuthenticationService service = new AuthenticationService(userServicePort, jwtTokenPort);

        assertEquals(201, service.registra(new RegistraRequest()).statusCode());

        when(userServicePort.createUtente(org.mockito.ArgumentMatchers.any(RegistraRequest.class)))
                .thenReturn(GatewayResponse.<UtenteResponse>of(400, null));
        assertEquals(400, service.registra(new RegistraRequest()).statusCode());
    }

    private UtenteResponse user() {
        return new UtenteResponse("admin", "Ada", "Lovelace", "ada@example.com", 1000);
    }

    private UtenteConRuoliResponse profile() {
        return new UtenteConRuoliResponse(
                "admin", "Ada", "Lovelace", "ada@example.com", 1000,
                List.of(new RuoloResponse("admin", "Administrator", 2)), "secret");
    }
}
