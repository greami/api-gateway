package org.api.gateway.application.port.contract;

import org.api.gateway.application.model.GatewayResponse;
import org.openapitools.model.*;

import java.time.LocalDateTime;
import java.util.List;

public final class CoreOperations {
    private CoreOperations() {
    }

    public interface Events {
        GatewayResponse<Void> cancellaEvento(Integer idEvento);
        GatewayResponse<Evento> cercaEvento(Integer idEvento);
        GatewayResponse<Evento> creaEvento(CreaEventoRequest request);
        GatewayResponse<List<Evento>> eventiAll();
        GatewayResponse<List<Evento>> eventiByData(LocalDateTime dataInizio, LocalDateTime dataFine);
        GatewayResponse<List<Evento>> cercaEventoByNome(String nomeEvento);
        GatewayResponse<Evento> modificaEvento(Integer idEvento, ModificaEventoRequest request);
    }

    public interface Works {
        GatewayResponse<Void> cancellaOpera(Integer idOpera);
        GatewayResponse<Opera> cercaOperaById(Integer idOpera);
        GatewayResponse<Opera> creaOpera(CreaOperaRequest request);
        GatewayResponse<Void> modificaOpera(Integer idOpera, ModificaOperaRequest request);
        GatewayResponse<List<Opera>> opereAll();
        GatewayResponse<List<Opera>> cercaOpereByIdEvento(Integer idEvento);
        GatewayResponse<List<Opera>> cercaOpereByNome(String nomeOpera);
        GatewayResponse<List<Opera>> cercaOpereByAutore(String autoreOpera);
    }

    public interface EventWorks {
        GatewayResponse<Void> cancellaEventoOpera(Integer idEvento, Integer idOpera);
        GatewayResponse<List<EventoOpera>> cercaEventiPerOpera(Integer idOpera);
        GatewayResponse<EventoOpera> cercaEventoOperaById(Integer idEvento, Integer idOpera);
        GatewayResponse<List<EventoOpera>> cercaOperePerEvento(Integer idEvento);
        GatewayResponse<EventoOpera> creaEventoOpera(CreaEventoOperaRequest request);
        GatewayResponse<Void> modificaEventoOpera(
                Integer idEvento, Integer idOpera, ModificaEventoOperaRequest request);
    }

    public interface EventFeedback {
        GatewayResponse<Void> cancellaFeedbackEvento(Integer idFeedback);
        GatewayResponse<List<FeedbackEventi>> cercaFeedbackEventiPerEvento(Integer idEvento);
        GatewayResponse<FeedbackEventi> cercaFeedbackEventoById(Integer idFeedback);
        GatewayResponse<List<FeedbackEventi>> cercaFeedbackEventoByIdUtente(Integer idUtente);
        GatewayResponse<FeedbackEventi> creaFeedbackEvento(CreaFeedbackEventiRequest request);
        GatewayResponse<Void> modificaFeedbackEvento(Integer idFeedback, ModificaFeedbackEventiRequest request);
    }

    public interface WorkFeedback {
        GatewayResponse<Void> cancellaFeedbackOpera(Integer idFeedback);
        GatewayResponse<FeedbackOpere> cercaFeedbackOperaById(Integer idFeedback);
        GatewayResponse<List<FeedbackOpere>> cercaFeedbackOperaByIdUtente(Integer idUtente);
        GatewayResponse<List<FeedbackOpere>> cercaFeedbackOperePerOpera(Integer idOpera);
        GatewayResponse<FeedbackOpere> creaFeedbackOpera(CreaFeedbackOpereRequest request);
        GatewayResponse<Void> modificaFeedbackOpera(Integer idFeedback, ModificaFeedbackOpereRequest request);
    }

    public interface Bookings {
        GatewayResponse<Void> cancellaPrenotazione(Integer idPrenotazione);
        GatewayResponse<Prenotazione> cercaPrenotazioneById(Integer idPrenotazione);
        GatewayResponse<List<Prenotazione>> cercaPrenotazioneByIdUtente(Integer idUtente);
        GatewayResponse<List<Prenotazione>> cercaPrenotazioniPerEvento(Integer idEvento);
        GatewayResponse<PrenotazioniDisponibiliEvento> cercaPrenotazioniDisponibiliPerEvento(Integer idEvento);
        GatewayResponse<Prenotazione> creaPrenotazione(CreaPrenotazioneRequest request);
        GatewayResponse<List<Prenotazione>> prenotazioniByData(LocalDateTime dataInizio, LocalDateTime dataFine);
        GatewayResponse<Void> modificaPrenotazione(Integer idPrenotazione, ModificaPrenotazioneRequest request);
    }
}
