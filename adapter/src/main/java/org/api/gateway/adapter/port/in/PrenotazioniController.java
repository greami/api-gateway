package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.BookingsUseCase;
import org.openapitools.api.PrenotazioniApi;
import org.openapitools.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class PrenotazioniController implements PrenotazioniApi {
    private final BookingsUseCase useCase;

    public PrenotazioniController(BookingsUseCase useCase) {
        this.useCase = useCase;
    }

    @Override
    public ResponseEntity<Void> cancellaPrenotazione(Integer idPrenotazione) {
        return GatewayResponseMapper.toResponseEntity(useCase.cancellaPrenotazione(idPrenotazione));
    }

    @Override
    public ResponseEntity<Prenotazione> cercaPrenotazioneById(Integer idPrenotazione) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaPrenotazioneById(idPrenotazione));
    }

    @Override
    public ResponseEntity<List<Prenotazione>> cercaPrenotazioneByIdUtente(Integer idUtente) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaPrenotazioneByIdUtente(idUtente));
    }

    @Override
    public ResponseEntity<PrenotazioniDisponibiliEvento> cercaPrenotazioniDisponibiliPerEvento(Integer idEvento) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaPrenotazioniDisponibiliPerEvento(idEvento));
    }

    @Override
    public ResponseEntity<List<Prenotazione>> cercaPrenotazioniPerEvento(Integer idEvento) {
        return GatewayResponseMapper.toResponseEntity(useCase.cercaPrenotazioniPerEvento(idEvento));
    }

    @Override
    public ResponseEntity<Prenotazione> creaPrenotazione(CreaPrenotazioneRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.creaPrenotazione(request));
    }

    @Override
    public ResponseEntity<List<Prenotazione>> prenotazioniByData(
            LocalDateTime dataInizio, LocalDateTime dataFine) {
        return GatewayResponseMapper.toResponseEntity(useCase.prenotazioniByData(dataInizio, dataFine));
    }

    @Override
    public ResponseEntity<Void> modificaPrenotazione(
            Integer idPrenotazione, ModificaPrenotazioneRequest request) {
        return GatewayResponseMapper.toResponseEntity(useCase.modificaPrenotazione(idPrenotazione, request));
    }
}
