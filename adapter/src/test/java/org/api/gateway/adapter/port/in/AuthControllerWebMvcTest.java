package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.UserServiceClient;
import org.api.gateway.adapter.security.CustomUserDetailsService;
import org.api.gateway.adapter.security.JwtService;
import org.junit.jupiter.api.Test;
import org.openapitools.model.AuthResponse;
import org.openapitools.model.UtenteConRuoliResponse;
import org.openapitools.model.UtenteResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserServiceClient userServiceClient;

    @Test
    void loginReturnsBearerTokenAndUserDetails() throws Exception {
        UserDetails user = User.withUsername("user01").password("secret").authorities("ROLE_USER").build();
        when(customUserDetailsService.loadByUserNameAndPassword("user01", "secret")).thenReturn(user);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("signed-token");
        when(userServiceClient.getUtenteByNomeUtente("user01")).thenReturn(ResponseEntity.ok(userProfile()));

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
        when(userServiceClient.createUtente(any(org.openapitools.model.RegistraRequest.class)))
                .thenReturn(ResponseEntity.status(201).body(new UtenteResponse("user01", "Luca", "Rossi",
                        "user@example.com", 1000)));
        mockMvc.perform(post("/auth/registra").contentType(MediaType.APPLICATION_JSON).content(registrationBody()))
                .andExpect(status().isCreated());

        when(userServiceClient.createUtente(any(org.openapitools.model.RegistraRequest.class)))
                .thenReturn(ResponseEntity.badRequest().build());
        mockMvc.perform(post("/auth/registra").contentType(MediaType.APPLICATION_JSON).content(registrationBody()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidLoginRequestIsRejectedAndCredentialRecoveryRouteIsMapped() throws Exception {
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

    private UtenteConRuoliResponse userProfile() {
        return new UtenteConRuoliResponse("user01", "Luca", "Rossi", "user@example.com", 1000, List.of(), "secret");
    }

    private String registrationBody() throws Exception {
        return """
                {"nomeUtente":"user01","nome":"Luca","cognome":"Rossi","email":"user@example.com","password":"secret"}
                """;
    }
}
