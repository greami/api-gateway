package org.api.gateway.adapter.port.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.api.gateway.adapter.client.CoreServiceClient;
import org.api.gateway.adapter.port.out.CoreServiceAdapter;
import org.api.gateway.application.service.CoreGatewayService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
        EventiController.class,
        OpereController.class,
        EventoOperaController.class,
        FeedbackEventiController.class,
        FeedbackOpereController.class,
        PrenotazioniController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import({CoreGatewayService.class, CoreServiceAdapter.class})
class CoreControllersWebMvcTest {
    private static final String LONG_NAME = "x".repeat(1000);
    private static final LocalDateTime DATE = LocalDateTime.parse("2026-01-01T10:00:00");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoreServiceClient coreServiceClient;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void eventiReadEndpointsReturnJson() throws Exception {
        Evento evento = evento();
        when(coreServiceClient.eventiAll()).thenReturn(ResponseEntity.ok(List.of(evento)));
        when(coreServiceClient.eventiByData(any(), any())).thenReturn(ResponseEntity.ok(List.of(evento)));
        when(coreServiceClient.cercaEventoByNome(LONG_NAME)).thenReturn(ResponseEntity.ok(List.of(evento)));
        when(coreServiceClient.cercaEvento(1000)).thenReturn(ResponseEntity.ok(evento));

        mockMvc.perform(get("/eventi"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idEvento").value(1000));
        mockMvc.perform(get("/eventi/by-data")
                        .param("dataInizio", DATE.toString())
                        .param("dataFine", DATE.plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idEvento").value(1000));
        mockMvc.perform(get("/eventi/by-nome/{nome}", LONG_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idEvento").value(1000));
        mockMvc.perform(get("/eventi/1000"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idEvento").value(1000));
    }

    @Test
    void eventiWriteEndpointsDelegateAndReturnStatuses() throws Exception {
        when(coreServiceClient.creaEvento(any()))
                .thenReturn(ResponseEntity.status(201).header("X-Upstream", "core").body(evento()));
        when(coreServiceClient.modificaEvento(any(), any())).thenReturn(ResponseEntity.ok(evento()));
        when(coreServiceClient.cancellaEvento(1000)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/eventi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("numeroPosti", 30, "data", DATE.toString(), "durata", 60, "nome", LONG_NAME))))
                .andExpect(status().isCreated())
                .andExpect(header().string("X-Upstream", "core"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idEvento").value(1000));
        mockMvc.perform(patch("/eventi/1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idEvento", 1000, "nome", LONG_NAME, "numeroPosti", 30, "data", DATE.toString(), "durata", 60))))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idEvento").value(1000));
        mockMvc.perform(delete("/eventi/1000")).andExpect(status().isNoContent());
    }

    @Test
    void opereReadEndpointsReturnJson() throws Exception {
        Opera opera = opera();
        when(coreServiceClient.opereAll()).thenReturn(ResponseEntity.ok(List.of(opera)));
        when(coreServiceClient.cercaOpereByIdEvento(1000)).thenReturn(ResponseEntity.ok(List.of(opera)));
        when(coreServiceClient.cercaOpereByNome("Guernica")).thenReturn(ResponseEntity.ok(List.of(opera)));
        when(coreServiceClient.cercaOpereByAutore("Picasso")).thenReturn(ResponseEntity.ok(List.of(opera)));
        when(coreServiceClient.cercaOperaById(1000)).thenReturn(ResponseEntity.ok(opera));

        mockMvc.perform(get("/opere"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idOpera").value(1000));
        mockMvc.perform(get("/opere/by-evento/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idOpera").value(1000));
        mockMvc.perform(get("/opere/by-nome/Guernica"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idOpera").value(1000));
        mockMvc.perform(get("/opere/by-autore/Picasso"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idOpera").value(1000));
        mockMvc.perform(get("/opere/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idOpera").value(1000));
    }

    @Test
    void opereWriteEndpointsDelegateAndReturnStatuses() throws Exception {
        when(coreServiceClient.creaOpera(any())).thenReturn(ResponseEntity.status(201).body(opera()));
        when(coreServiceClient.modificaOpera(any(), any())).thenReturn(ResponseEntity.ok().build());
        when(coreServiceClient.cancellaOpera(1000)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/opere")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Guernica", "autore", "Picasso", "tipo", 1))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idOpera").value(1000));
        mockMvc.perform(patch("/opere/1000")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("nome", "Guernica", "autore", "Picasso", "tipo", 1))))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/opere/1000")).andExpect(status().isNoContent());
    }

    @Test
    void eventoOperaEndpointsReturnJsonAndStatuses() throws Exception {
        EventoOpera relation = new EventoOpera(1000, 1000);
        when(coreServiceClient.creaEventoOpera(any())).thenReturn(ResponseEntity.status(201).body(relation));
        when(coreServiceClient.cercaEventoOperaById(1000, 1000)).thenReturn(ResponseEntity.ok(relation));
        when(coreServiceClient.cercaOperePerEvento(1000)).thenReturn(ResponseEntity.ok(List.of(relation)));
        when(coreServiceClient.cercaEventiPerOpera(1000)).thenReturn(ResponseEntity.ok(List.of(relation)));
        when(coreServiceClient.modificaEventoOpera(any(), any(), any())).thenReturn(ResponseEntity.ok().build());
        when(coreServiceClient.cancellaEventoOpera(1000, 1000)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/evento-opera").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idEvento", 1000, "idOpera", 1000))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idEvento").value(1000));
        mockMvc.perform(get("/evento-opera/1000/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idOpera").value(1000));
        mockMvc.perform(get("/eventi/1000/opere"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idEvento").value(1000));
        mockMvc.perform(get("/opere/1000/eventi"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idOpera").value(1000));
        mockMvc.perform(patch("/evento-opera/1000/1000").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idOperaNuova", 1001))))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/evento-opera/1000/1000")).andExpect(status().isNoContent());
    }

    @Test
    void feedbackEventiEndpointsReturnJsonAndStatuses() throws Exception {
        FeedbackEventi feedback = new FeedbackEventi(1000, 1000, 1000, 5, "Ottimo");
        when(coreServiceClient.creaFeedbackEvento(any())).thenReturn(ResponseEntity.status(201).body(feedback));
        when(coreServiceClient.cercaFeedbackEventoById(1000)).thenReturn(ResponseEntity.ok(feedback));
        when(coreServiceClient.cercaFeedbackEventoByIdUtente(1000)).thenReturn(ResponseEntity.ok(List.of(feedback)));
        when(coreServiceClient.cercaFeedbackEventiPerEvento(1000)).thenReturn(ResponseEntity.ok(List.of(feedback)));
        when(coreServiceClient.modificaFeedbackEvento(any(), any())).thenReturn(ResponseEntity.ok().build());
        when(coreServiceClient.cancellaFeedbackEvento(1000)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/feedback-eventi").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idUtente", 1000, "idEvento", 1000, "voto", 5, "note", "Ottimo"))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idFeedback").value(1000));
        mockMvc.perform(get("/feedback-eventi/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idFeedback").value(1000));
        mockMvc.perform(get("/feedback-eventi/cercaFeedbackEventoByIdUtente/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idUtente").value(1000));
        mockMvc.perform(get("/eventi/1000/feedback-eventi"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idEvento").value(1000));
        mockMvc.perform(patch("/feedback-eventi/1000").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("voto", 4, "note", "Buono"))))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/feedback-eventi/1000")).andExpect(status().isNoContent());
    }

    @Test
    void feedbackOpereEndpointsReturnJsonAndStatuses() throws Exception {
        FeedbackOpere feedback = new FeedbackOpere(1000, 1000, 1000, 5, "Ottimo");
        when(coreServiceClient.creaFeedbackOpera(any())).thenReturn(ResponseEntity.status(201).body(feedback));
        when(coreServiceClient.cercaFeedbackOperaById(1000)).thenReturn(ResponseEntity.ok(feedback));
        when(coreServiceClient.cercaFeedbackOperaByIdUtente(1000)).thenReturn(ResponseEntity.ok(List.of(feedback)));
        when(coreServiceClient.cercaFeedbackOperePerOpera(1000)).thenReturn(ResponseEntity.ok(List.of(feedback)));
        when(coreServiceClient.modificaFeedbackOpera(any(), any())).thenReturn(ResponseEntity.ok().build());
        when(coreServiceClient.cancellaFeedbackOpera(1000)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/feedback-opere").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idUtente", 1000, "idOpera", 1000, "voto", 5, "note", "Ottimo"))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idFeedback").value(1000));
        mockMvc.perform(get("/feedback-opere/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idFeedback").value(1000));
        mockMvc.perform(get("/feedback-opere/cercaFeedbackOperaByIdUtente/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idUtente").value(1000));
        mockMvc.perform(get("/opere/1000/feedback-opere"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idOpera").value(1000));
        mockMvc.perform(patch("/feedback-opere/1000").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("voto", 4, "note", "Buono"))))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/feedback-opere/1000")).andExpect(status().isNoContent());
    }

    @Test
    void prenotazioneEndpointsReturnJsonAndStatuses() throws Exception {
        Prenotazione booking = new Prenotazione(1000, 1000, 1000, 2, DATE, DATE);
        when(coreServiceClient.creaPrenotazione(any())).thenReturn(ResponseEntity.status(201).body(booking));
        when(coreServiceClient.prenotazioniByData(any(), any())).thenReturn(ResponseEntity.ok(List.of(booking)));
        when(coreServiceClient.cercaPrenotazioneById(1000)).thenReturn(ResponseEntity.ok(booking));
        when(coreServiceClient.cercaPrenotazioneByIdUtente(1000)).thenReturn(ResponseEntity.ok(List.of(booking)));
        when(coreServiceClient.cercaPrenotazioniPerEvento(1000)).thenReturn(ResponseEntity.ok(List.of(booking)));
        when(coreServiceClient.cercaPrenotazioniDisponibiliPerEvento(1000)).thenReturn(ResponseEntity.ok(
                new PrenotazioniDisponibiliEvento(1000, 20, 18)));
        when(coreServiceClient.modificaPrenotazione(any(), any())).thenReturn(ResponseEntity.ok().build());
        when(coreServiceClient.cancellaPrenotazione(1000)).thenReturn(ResponseEntity.noContent().build());

        mockMvc.perform(post("/prenotazioni").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("idUtente", 1000, "idEvento", 1000, "numeroPartecipanti", 2))))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idPrenotazione").value(1000));
        mockMvc.perform(get("/prenotazioni/by-data").param("dataInizio", DATE.toString())
                        .param("dataFine", DATE.plusDays(1).toString()))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idPrenotazione").value(1000));
        mockMvc.perform(get("/prenotazioni/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.idPrenotazione").value(1000));
        mockMvc.perform(get("/prenotazioni/cercaPrenotazioneByIdUtente/1000"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idUtente").value(1000));
        mockMvc.perform(get("/eventi/1000/prenotazioni"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].idEvento").value(1000));
        mockMvc.perform(get("/eventi/1000/prenotazioni/disponibili"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.numeroPostiDispobili").value(18));
        mockMvc.perform(patch("/prenotazioni/1000").contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of("numeroPartecipanti", 3))))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/prenotazioni/1000")).andExpect(status().isNoContent());
    }

    @Test
    void forwardsNegativeStatusesAndRejectsMalformedPathIds() throws Exception {
        when(coreServiceClient.cercaEvento(1000)).thenReturn(ResponseEntity.notFound().build());
        when(coreServiceClient.cercaOperaById(1000)).thenReturn(ResponseEntity.internalServerError().build());

        mockMvc.perform(get("/eventi/1000")).andExpect(status().isNotFound());
        mockMvc.perform(get("/opere/1000")).andExpect(status().isInternalServerError());
        mockMvc.perform(get("/eventi/not-an-id")).andExpect(status().isBadRequest());
    }

    private Evento evento() {
        return new Evento(1000, LONG_NAME, 30, DATE, 60);
    }

    private Opera opera() {
        return new Opera(1000, "Guernica", "Picasso", 1);
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }
}
