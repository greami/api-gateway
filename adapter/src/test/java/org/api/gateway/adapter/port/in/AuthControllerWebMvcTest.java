package org.api.gateway.adapter.port.in;

import org.api.gateway.application.model.GatewayResponse;
import org.api.gateway.application.port.in.AuthenticationUseCase;
import org.api.gateway.adapter.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationUseCase authenticationUseCase;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void loginReturnsBearerTokenAndUserDetails() throws Exception {
        when(authenticationUseCase.login(any()))
                .thenReturn(new AuthResponse("signed-token", "Bearer", "user01", List.of("ROLE_USER"), 1000));

        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"user01","password":"secret"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").value("signed-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("user01"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"))
                .andExpect(jsonPath("$.idUtente").value(1000));
    }

    @Test
    void registrationReturnsCreatedOrBadRequest() throws Exception {
        when(authenticationUseCase.registra(any(RegistraRequest.class)))
                .thenReturn(GatewayResponse.of(201, null));
        mockMvc.perform(post("/auth/registra").contentType(MediaType.APPLICATION_JSON).content(registrationBody()))
                .andExpect(status().isCreated());

        when(authenticationUseCase.registra(any(RegistraRequest.class)))
                .thenReturn(GatewayResponse.of(400, null));
        mockMvc.perform(post("/auth/registra").contentType(MediaType.APPLICATION_JSON).content(registrationBody()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidLoginRequestIsRejectedAndCredentialRecoveryRouteIsMapped() throws Exception {
        when(authenticationUseCase.recuperaCredenziali(any(RecuperaCredenzialiRequest.class)))
                .thenReturn(GatewayResponse.of(200, null));
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/auth/recupera-credenziali").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"user@example.com"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    private String registrationBody() throws Exception {
        return """
                {"nomeUtente":"user01","nome":"Luca","cognome":"Rossi","email":"user@example.com","password":"secret"}
                """;
    }
}
