package org.api.gateway.application.service;

import org.api.gateway.application.model.GatewayResponse;
import org.api.gateway.application.port.in.BookingsUseCase;
import org.api.gateway.application.port.in.EventFeedbackUseCase;
import org.api.gateway.application.port.in.EventWorksUseCase;
import org.api.gateway.application.port.in.EventsUseCase;
import org.api.gateway.application.port.in.WorkFeedbackUseCase;
import org.api.gateway.application.port.in.WorksUseCase;
import org.api.gateway.application.port.out.CoreServicePort;
import org.openapitools.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CoreGatewayService implements EventsUseCase, WorksUseCase, EventWorksUseCase,
        EventFeedbackUseCase, WorkFeedbackUseCase, BookingsUseCase {
    private final CoreServicePort coreServicePort;

    public CoreGatewayService(CoreServicePort coreServicePort) {
        this.coreServicePort = coreServicePort;
    }

    @Override
    public GatewayResponse<Void> cancellaEvento(Integer idEvento) {
        return coreServicePort.cancellaEvento(idEvento);
    }

    @Override
    public GatewayResponse<Evento> cercaEvento(Integer idEvento) {
        return coreServicePort.cercaEvento(idEvento);
    }

    @Override
    public GatewayResponse<Evento> creaEvento(CreaEventoRequest request) {
        return coreServicePort.creaEvento(request);
    }

    @Override
    public GatewayResponse<List<Evento>> eventiAll() {
        return coreServicePort.eventiAll();
    }

    @Override
    public GatewayResponse<List<Evento>> eventiByData(LocalDateTime dataInizio, LocalDateTime dataFine) {
        return coreServicePort.eventiByData(dataInizio, dataFine);
    }

    @Override
    public GatewayResponse<List<Evento>> cercaEventoByNome(String nomeEvento) {
        return coreServicePort.cercaEventoByNome(nomeEvento);
    }

    @Override
    public GatewayResponse<Evento> modificaEvento(Integer idEvento, ModificaEventoRequest request) {
        return coreServicePort.modificaEvento(idEvento, request);
    }

    @Override
    public GatewayResponse<Void> cancellaOpera(Integer idOpera) {
        return coreServicePort.cancellaOpera(idOpera);
    }

    @Override
    public GatewayResponse<Opera> cercaOperaById(Integer idOpera) {
        return coreServicePort.cercaOperaById(idOpera);
    }

    @Override
    public GatewayResponse<Opera> creaOpera(CreaOperaRequest request) {
        return coreServicePort.creaOpera(request);
    }

    @Override
    public GatewayResponse<Void> modificaOpera(Integer idOpera, ModificaOperaRequest request) {
        return coreServicePort.modificaOpera(idOpera, request);
    }

    @Override
    public GatewayResponse<List<Opera>> opereAll() {
        return coreServicePort.opereAll();
    }

    @Override
    public GatewayResponse<List<Opera>> cercaOpereByIdEvento(Integer idEvento) {
        return coreServicePort.cercaOpereByIdEvento(idEvento);
    }

    @Override
    public GatewayResponse<List<Opera>> cercaOpereByNome(String nomeOpera) {
        return coreServicePort.cercaOpereByNome(nomeOpera);
    }

    @Override
    public GatewayResponse<List<Opera>> cercaOpereByAutore(String autoreOpera) {
        return coreServicePort.cercaOpereByAutore(autoreOpera);
    }

    @Override
    public GatewayResponse<Void> cancellaEventoOpera(Integer idEvento, Integer idOpera) {
        return coreServicePort.cancellaEventoOpera(idEvento, idOpera);
    }

    @Override
    public GatewayResponse<List<EventoOpera>> cercaEventiPerOpera(Integer idOpera) {
        return coreServicePort.cercaEventiPerOpera(idOpera);
    }

    @Override
    public GatewayResponse<EventoOpera> cercaEventoOperaById(Integer idEvento, Integer idOpera) {
        return coreServicePort.cercaEventoOperaById(idEvento, idOpera);
    }

    @Override
    public GatewayResponse<List<EventoOpera>> cercaOperePerEvento(Integer idEvento) {
        return coreServicePort.cercaOperePerEvento(idEvento);
    }

    @Override
    public GatewayResponse<EventoOpera> creaEventoOpera(CreaEventoOperaRequest request) {
        return coreServicePort.creaEventoOpera(request);
    }

    @Override
    public GatewayResponse<Void> modificaEventoOpera(
            Integer idEvento, Integer idOpera, ModificaEventoOperaRequest request) {
        return coreServicePort.modificaEventoOpera(idEvento, idOpera, request);
    }

    @Override
    public GatewayResponse<Void> cancellaFeedbackEvento(Integer idFeedback) {
        return coreServicePort.cancellaFeedbackEvento(idFeedback);
    }

    @Override
    public GatewayResponse<List<FeedbackEventi>> cercaFeedbackEventiPerEvento(Integer idEvento) {
        return coreServicePort.cercaFeedbackEventiPerEvento(idEvento);
    }

    @Override
    public GatewayResponse<FeedbackEventi> cercaFeedbackEventoById(Integer idFeedback) {
        return coreServicePort.cercaFeedbackEventoById(idFeedback);
    }

    @Override
    public GatewayResponse<List<FeedbackEventi>> cercaFeedbackEventoByIdUtente(Integer idUtente) {
        return coreServicePort.cercaFeedbackEventoByIdUtente(idUtente);
    }

    @Override
    public GatewayResponse<FeedbackEventi> creaFeedbackEvento(CreaFeedbackEventiRequest request) {
        return coreServicePort.creaFeedbackEvento(request);
    }

    @Override
    public GatewayResponse<Void> modificaFeedbackEvento(
            Integer idFeedback, ModificaFeedbackEventiRequest request) {
        return coreServicePort.modificaFeedbackEvento(idFeedback, request);
    }

    @Override
    public GatewayResponse<Void> cancellaFeedbackOpera(Integer idFeedback) {
        return coreServicePort.cancellaFeedbackOpera(idFeedback);
    }

    @Override
    public GatewayResponse<FeedbackOpere> cercaFeedbackOperaById(Integer idFeedback) {
        return coreServicePort.cercaFeedbackOperaById(idFeedback);
    }

    @Override
    public GatewayResponse<List<FeedbackOpere>> cercaFeedbackOperaByIdUtente(Integer idUtente) {
        return coreServicePort.cercaFeedbackOperaByIdUtente(idUtente);
    }

    @Override
    public GatewayResponse<List<FeedbackOpere>> cercaFeedbackOperePerOpera(Integer idOpera) {
        return coreServicePort.cercaFeedbackOperePerOpera(idOpera);
    }

    @Override
    public GatewayResponse<FeedbackOpere> creaFeedbackOpera(CreaFeedbackOpereRequest request) {
        return coreServicePort.creaFeedbackOpera(request);
    }

    @Override
    public GatewayResponse<Void> modificaFeedbackOpera(
            Integer idFeedback, ModificaFeedbackOpereRequest request) {
        return coreServicePort.modificaFeedbackOpera(idFeedback, request);
    }

    @Override
    public GatewayResponse<Void> cancellaPrenotazione(Integer idPrenotazione) {
        return coreServicePort.cancellaPrenotazione(idPrenotazione);
    }

    @Override
    public GatewayResponse<Prenotazione> cercaPrenotazioneById(Integer idPrenotazione) {
        return coreServicePort.cercaPrenotazioneById(idPrenotazione);
    }

    @Override
    public GatewayResponse<List<Prenotazione>> cercaPrenotazioneByIdUtente(Integer idUtente) {
        try {
            return coreServicePort.cercaPrenotazioneByIdUtente(idUtente);
        } catch (Exception exception) {
            return GatewayResponse.of(200, new ArrayList<>());
        }
    }

    @Override
    public GatewayResponse<List<Prenotazione>> cercaPrenotazioniPerEvento(Integer idEvento) {
        return coreServicePort.cercaPrenotazioniPerEvento(idEvento);
    }

    @Override
    public GatewayResponse<PrenotazioniDisponibiliEvento> cercaPrenotazioniDisponibiliPerEvento(Integer idEvento) {
        try {
            return coreServicePort.cercaPrenotazioniDisponibiliPerEvento(idEvento);
        } catch (Exception exception) {
            PrenotazioniDisponibiliEvento availability = new PrenotazioniDisponibiliEvento();
            availability.setIdEvento(idEvento);
            availability.setNumeroPostiDispobili(0);
            availability.setNumeroPostiTotali(0);
            return GatewayResponse.of(200, availability);
        }
    }

    @Override
    public GatewayResponse<Prenotazione> creaPrenotazione(CreaPrenotazioneRequest request) {
        return coreServicePort.creaPrenotazione(request);
    }

    @Override
    public GatewayResponse<List<Prenotazione>> prenotazioniByData(
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        return coreServicePort.prenotazioniByData(dataInizio, dataFine);
    }

    @Override
    public GatewayResponse<Void> modificaPrenotazione(
            Integer idPrenotazione, ModificaPrenotazioneRequest request) {
        return coreServicePort.modificaPrenotazione(idPrenotazione, request);
    }
}
