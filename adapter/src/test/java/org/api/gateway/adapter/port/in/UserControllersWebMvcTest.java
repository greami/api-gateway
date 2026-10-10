package org.api.gateway.adapter.port.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.api.gateway.adapter.client.UserServiceClient;
import org.api.gateway.adapter.port.out.UserServiceAdapter;
import org.api.gateway.application.service.UserGatewayService;
import org.api.gateway.adapter.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.openapitools.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        UtentiController.class,
        RuoliController.class,
        UtenteRuoliController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import({UserGatewayService.class, UserServiceAdapter.class})
class UserControllersWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void utentiReadEndpointsReturnJson() throws Exception {
        UtenteResponse user = user();
        UtenteConRuoliResponse userWithRoles = userWithRoles();
        when(userServiceClient.listUtenti()).thenReturn(ResponseEntity.ok(List.of(user)));
        when(userServiceClient.getUtenteById(1000)).thenReturn(ResponseEntity.ok(userWithRoles));
        when(userServiceClient.cercaUtentiLikeNome("user")).thenReturn(ResponseEntity.ok(List.of(userWithRoles)));
        when(userServiceClient.getUtenteByEmail("user@example.com")).thenReturn(ResponseEntity.ok(userWithRoles));
        when(userServiceClient.getUtenteByNomeUtente("user01")).thenReturn(ResponseEntity.ok(userWithRoles));
        when(userServiceClient.getUtenteByUserNameAndPassword("user01", "secret")).thenReturn(ResponseEntity.ok(user));

        mockMvc.perform(get("/utenti"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idUtente").value(1000));
        mockMvc.perform(get("/utenti/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nomeUtente").value("user01"));
        mockMvc.perform(get("/utenti/cercaByNomeUtenteLike").param("nomeUtente", "user"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idUtente").value(1000));
        mockMvc.perform(get("/utenti/email/user@example.com"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtente").value(1000));
        mockMvc.perform(get("/utenti/nome/user01"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtente").value(1000));
        mockMvc.perform(get("/utenti/credentials").param("userName", "user01").param("password", "secret"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtente").value(1000));
    }

    @Test
    void utentiWriteEndpointsDelegateAndReturnStatuses() throws Exception {
        when(userServiceClient.createUtente(any(UtenteCreateRequest.class))).thenReturn(ResponseEntity.status(201).body(user()));
        when(userServiceClient.updateUtente(anyInt(), any())).thenReturn(ResponseEntity.ok(user()));
        when(userServiceClient.deleteUtente(1000)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/utenti").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nomeUtente", "user01", "nome", "Luca", "cognome", "Rossi",
                                "email", "user@example.com", "password", "secret"))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtente").value(1000));
        mockMvc.perform(put("/utenti/1000").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nomeUtente", "user01", "nome", "Luca", "cognome", "Rossi",
                                "email", "user@example.com", "password", "secret"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("user@example.com"));
        mockMvc.perform(delete("/utenti/1000")).andExpect(status().isNoContent());
    }

    @Test
    void ruoliReadEndpointsReturnJson() throws Exception {
        RuoloResponse role = role();
        when(userServiceClient.listRuoli()).thenReturn(ResponseEntity.ok(List.of(role)));
        when(userServiceClient.getRuoloById(1)).thenReturn(ResponseEntity.ok(role));

        mockMvc.perform(get("/ruoli"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idRuolo").value(1));
        mockMvc.perform(get("/ruoli/1"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("admin"));
    }

    @Test
    void ruoliWriteEndpointsDelegateAndReturnStatuses() throws Exception {
        when(userServiceClient.createRuolo(any())).thenReturn(ResponseEntity.status(201).body(role()));
        when(userServiceClient.updateRuolo(anyInt(), any())).thenReturn(ResponseEntity.ok(role()));
        when(userServiceClient.deleteRuolo(1)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/ruoli").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idRuolo", 1, "nome", "admin", "descrizione", "Administrator"))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idRuolo").value(1));
        mockMvc.perform(put("/ruoli/1").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idRuolo", 1, "nome", "admin", "descrizione", "Administrator"))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.nome").value("admin"));
        mockMvc.perform(delete("/ruoli/1")).andExpect(status().isNoContent());
    }

    @Test
    void utenteRuoliReadEndpointsReturnJson() throws Exception {
        UtenteRuoloResponse relation = new UtenteRuoloResponse(1000, 1, 2000);
        when(userServiceClient.listUtenteRuoli()).thenReturn(ResponseEntity.ok(List.of(relation)));
        when(userServiceClient.getUtenteRuoloById(2000)).thenReturn(ResponseEntity.ok(relation));
        when(userServiceClient.getUtenteRuoloByUtenteAndRuolo(1000, 1)).thenReturn(ResponseEntity.ok(relation));
        when(userServiceClient.getRuoliByUtente(1000)).thenReturn(ResponseEntity.ok(List.of(1)));

        mockMvc.perform(get("/utente-ruoli"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idUtenteRuolo").value(2000));
        mockMvc.perform(get("/utente-ruoli/2000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtente").value(1000));
        mockMvc.perform(get("/utente-ruoli/cerca").param("idUtente", "1000").param("idRuolo", "1"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idRuolo").value(1));
        mockMvc.perform(get("/utente-ruoli/utente/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value(1));
    }

    @Test
    void utenteRuoliWriteEndpointsDelegateAndReturnStatuses() throws Exception {
        UtenteRuoloResponse relation = new UtenteRuoloResponse(1000, 1, 2000);
        when(userServiceClient.createUtenteRuolo(any())).thenReturn(ResponseEntity.status(201).body(relation));
        when(userServiceClient.updateUtenteRuolo(anyInt(), any())).thenReturn(ResponseEntity.ok(relation));
        when(userServiceClient.deleteUtenteRuolo(2000)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/utente-ruoli").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idUtente", 1000, "idRuolo", 1))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idUtenteRuolo").value(2000));
        mockMvc.perform(put("/utente-ruoli/2000").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idUtente", 1000, "idRuolo", 1))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idRuolo").value(1));
        mockMvc.perform(delete("/utente-ruoli/2000")).andExpect(status().isNoContent());
    }

    @Test
    void forwardsNegativeStatusesAndRejectsMalformedPathIds() throws Exception {
        when(userServiceClient.getUtenteById(1000)).thenReturn(ResponseEntity.notFound().build());
        when(userServiceClient.getRuoloById(1)).thenReturn(ResponseEntity.internalServerError().build());

        mockMvc.perform(get("/utenti/1000")).andExpect(status().isNotFound());
        mockMvc.perform(get("/ruoli/1")).andExpect(status().isInternalServerError());
        mockMvc.perform(get("/utenti/not-an-id")).andExpect(status().isBadRequest());
    }

    private UtenteResponse user() {
        return new UtenteResponse("user01", "Luca", "Rossi", "user@example.com", 1000);
    }

    private UtenteConRuoliResponse userWithRoles() {
        return new UtenteConRuoliResponse("user01", "Luca", "Rossi", "user@example.com", 1000, List.of(role()), "secret");
    }

    private RuoloResponse role() {
        return new RuoloResponse("admin", "Administrator", 1);
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
