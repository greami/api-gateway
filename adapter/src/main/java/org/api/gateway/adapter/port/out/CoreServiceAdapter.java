package org.api.gateway.adapter.port.out;

import feign.FeignException;
import org.api.gateway.adapter.client.CoreServiceClient;
import org.api.gateway.application.model.GatewayResponse;
import org.api.gateway.application.port.out.CoreServicePort;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

@Component
public class CoreServiceAdapter implements CoreServicePort {
    private final CoreServiceClient client;

    public CoreServiceAdapter(CoreServiceClient client) {
        this.client = client;
    }

    @Override
    public GatewayResponse<Void> cancellaEvento(Integer idEvento) {
        return forward(() -> client.cancellaEvento(idEvento));
    }

    @Override
    public GatewayResponse<Evento> cercaEvento(Integer idEvento) {
        return forward(() -> client.cercaEvento(idEvento));
    }

    @Override
    public GatewayResponse<Evento> creaEvento(CreaEventoRequest request) {
        return forward(() -> client.creaEvento(request));
    }

    @Override
    public GatewayResponse<List<Evento>> eventiAll() {
        return forward(client::eventiAll);
    }

    @Override
    public GatewayResponse<List<Evento>> eventiByData(LocalDateTime dataInizio, LocalDateTime dataFine) {
        return forward(() -> client.eventiByData(dataInizio, dataFine));
    }

    @Override
    public GatewayResponse<List<Evento>> cercaEventoByNome(String nomeEvento) {
        return forward(() -> client.cercaEventoByNome(nomeEvento));
    }

    @Override
    public GatewayResponse<Evento> modificaEvento(Integer idEvento, ModificaEventoRequest request) {
        return forward(() -> client.modificaEvento(idEvento, request));
    }

    @Override
    public GatewayResponse<Void> cancellaOpera(Integer idOpera) {
        return forward(() -> client.cancellaOpera(idOpera));
    }

    @Override
    public GatewayResponse<Opera> cercaOperaById(Integer idOpera) {
        return forward(() -> client.cercaOperaById(idOpera));
    }

    @Override
    public GatewayResponse<Opera> creaOpera(CreaOperaRequest request) {
        return forward(() -> client.creaOpera(request));
    }

    @Override
    public GatewayResponse<Void> modificaOpera(Integer idOpera, ModificaOperaRequest request) {
        return forward(() -> client.modificaOpera(idOpera, request));
    }

    @Override
    public GatewayResponse<List<Opera>> opereAll() {
        return forward(client::opereAll);
    }

    @Override
    public GatewayResponse<List<Opera>> cercaOpereByIdEvento(Integer idEvento) {
        return forward(() -> client.cercaOpereByIdEvento(idEvento));
    }

    @Override
    public GatewayResponse<List<Opera>> cercaOpereByNome(String nomeOpera) {
        return forward(() -> client.cercaOpereByNome(nomeOpera));
    }

    @Override
    public GatewayResponse<List<Opera>> cercaOpereByAutore(String autoreOpera) {
        return forward(() -> client.cercaOpereByAutore(autoreOpera));
    }

    @Override
    public GatewayResponse<Void> cancellaEventoOpera(Integer idEvento, Integer idOpera) {
        return forward(() -> client.cancellaEventoOpera(idEvento, idOpera));
    }

    @Override
    public GatewayResponse<List<EventoOpera>> cercaEventiPerOpera(Integer idOpera) {
        return forward(() -> client.cercaEventiPerOpera(idOpera));
    }

    @Override
    public GatewayResponse<EventoOpera> cercaEventoOperaById(Integer idEvento, Integer idOpera) {
        return forward(() -> client.cercaEventoOperaById(idEvento, idOpera));
    }

    @Override
    public GatewayResponse<List<EventoOpera>> cercaOperePerEvento(Integer idEvento) {
        return forward(() -> client.cercaOperePerEvento(idEvento));
    }

    @Override
    public GatewayResponse<EventoOpera> creaEventoOpera(CreaEventoOperaRequest request) {
        return forward(() -> client.creaEventoOpera(request));
    }

    @Override
    public GatewayResponse<Void> modificaEventoOpera(
            Integer idEvento, Integer idOpera, ModificaEventoOperaRequest request) {
        return forward(() -> client.modificaEventoOpera(idEvento, idOpera, request));
    }

    @Override
    public GatewayResponse<Void> cancellaFeedbackEvento(Integer idFeedback) {
        return forward(() -> client.cancellaFeedbackEvento(idFeedback));
    }

    @Override
    public GatewayResponse<List<FeedbackEventi>> cercaFeedbackEventiPerEvento(Integer idEvento) {
        return forward(() -> client.cercaFeedbackEventiPerEvento(idEvento));
    }

    @Override
    public GatewayResponse<FeedbackEventi> cercaFeedbackEventoById(Integer idFeedback) {
        return forward(() -> client.cercaFeedbackEventoById(idFeedback));
    }

    @Override
    public GatewayResponse<List<FeedbackEventi>> cercaFeedbackEventoByIdUtente(Integer idUtente) {
        return forward(() -> client.cercaFeedbackEventoByIdUtente(idUtente));
    }

    @Override
    public GatewayResponse<FeedbackEventi> creaFeedbackEvento(CreaFeedbackEventiRequest request) {
        return forward(() -> client.creaFeedbackEvento(request));
    }

    @Override
    public GatewayResponse<Void> modificaFeedbackEvento(
            Integer idFeedback, ModificaFeedbackEventiRequest request) {
        return forward(() -> client.modificaFeedbackEvento(idFeedback, request));
    }

    @Override
    public GatewayResponse<Void> cancellaFeedbackOpera(Integer idFeedback) {
        return forward(() -> client.cancellaFeedbackOpera(idFeedback));
    }

    @Override
    public GatewayResponse<FeedbackOpere> cercaFeedbackOperaById(Integer idFeedback) {
        return forward(() -> client.cercaFeedbackOperaById(idFeedback));
    }

    @Override
    public GatewayResponse<List<FeedbackOpere>> cercaFeedbackOperaByIdUtente(Integer idUtente) {
        return forward(() -> client.cercaFeedbackOperaByIdUtente(idUtente));
    }

    @Override
    public GatewayResponse<List<FeedbackOpere>> cercaFeedbackOperePerOpera(Integer idOpera) {
        return forward(() -> client.cercaFeedbackOperePerOpera(idOpera));
    }

    @Override
    public GatewayResponse<FeedbackOpere> creaFeedbackOpera(CreaFeedbackOpereRequest request) {
        return forward(() -> client.creaFeedbackOpera(request));
    }

    @Override
    public GatewayResponse<Void> modificaFeedbackOpera(
            Integer idFeedback, ModificaFeedbackOpereRequest request) {
        return forward(() -> client.modificaFeedbackOpera(idFeedback, request));
    }

    @Override
    public GatewayResponse<Void> cancellaPrenotazione(Integer idPrenotazione) {
        return forward(() -> client.cancellaPrenotazione(idPrenotazione));
    }

    @Override
    public GatewayResponse<Prenotazione> cercaPrenotazioneById(Integer idPrenotazione) {
        return forward(() -> client.cercaPrenotazioneById(idPrenotazione));
    }

    @Override
    public GatewayResponse<List<Prenotazione>> cercaPrenotazioneByIdUtente(Integer idUtente) {
        return forward(() -> client.cercaPrenotazioneByIdUtente(idUtente));
    }

    @Override
    public GatewayResponse<List<Prenotazione>> cercaPrenotazioniPerEvento(Integer idEvento) {
        return forward(() -> client.cercaPrenotazioniPerEvento(idEvento));
    }

    @Override
    public GatewayResponse<PrenotazioniDisponibiliEvento> cercaPrenotazioniDisponibiliPerEvento(Integer idEvento) {
        return forward(() -> client.cercaPrenotazioniDisponibiliPerEvento(idEvento));
    }

    @Override
    public GatewayResponse<Prenotazione> creaPrenotazione(CreaPrenotazioneRequest request) {
        return forward(() -> client.creaPrenotazione(request));
    }

    @Override
    public GatewayResponse<List<Prenotazione>> prenotazioniByData(
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        return forward(() -> client.prenotazioniByData(dataInizio, dataFine));
    }

    @Override
    public GatewayResponse<Void> modificaPrenotazione(
            Integer idPrenotazione, ModificaPrenotazioneRequest request) {
        return forward(() -> client.modificaPrenotazione(idPrenotazione, request));
    }

    private <T> GatewayResponse<T> forward(Supplier<ResponseEntity<T>> call) {
        return GatewayResponseMapper.from(call.get());
    }
}
