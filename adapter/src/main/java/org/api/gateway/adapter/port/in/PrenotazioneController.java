package org.api.gateway.adapter.port.in;

import org.openapitools.api.DefaultApi;
import org.openapitools.model.CreaPrenotazioneRequest;
import org.openapitools.model.ModificaPrenotazioneRequest;
import org.openapitools.model.Prenotazione;
import org.api.gateway.adapter.mapper.PrenotazioneMapper;
import org.api.gateway.domain.model.userService.PrenotazioneRecord;
import org.api.gateway.domain.port.in.PrenotazioneUseCasePortIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class PrenotazioneController  implements DefaultApi
{
    private final PrenotazioneUseCasePortIn prenotazioneUseCasePortIn;
    private final PrenotazioneMapper prenotazioneMapper;

    public PrenotazioneController(PrenotazioneUseCasePortIn prenotazioneUseCasePortIn,
                                  PrenotazioneMapper prenotazioneMapper) {
        this.prenotazioneUseCasePortIn = prenotazioneUseCasePortIn;
        this.prenotazioneMapper = prenotazioneMapper;
    }

    @Override
    public ResponseEntity<Void> cancellaPrenotazione(Integer idPrenotazione) {
        return prenotazioneUseCasePortIn.deleteById(idPrenotazione) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Prenotazione> cercaPrenotazioneById(Integer idPrenotazione) {
        return prenotazioneUseCasePortIn.findById(idPrenotazione)
                .map(responseEntity -> ResponseEntity.ok(prenotazioneMapper.toPrenotazione(responseEntity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<Prenotazione>> cercaPrenotazioniPerEvento(Integer idEvento) {

        var listByIdEvento = prenotazioneUseCasePortIn.findByEvento(idEvento).stream()
            .map(prenotazioneRecord ->  {
              return  prenotazioneMapper.toPrenotazione(prenotazioneRecord);
            }).toList();

        return listByIdEvento.isEmpty()? ResponseEntity.notFound().build(): ResponseEntity.ok(listByIdEvento);
    }

    @Override
    public ResponseEntity<Prenotazione> creaPrenotazione(CreaPrenotazioneRequest creaPrenotazioneRequest) {
        return prenotazioneUseCasePortIn.save(
                new PrenotazioneRecord(null,
                        creaPrenotazioneRequest.getIdUtente(),
                        creaPrenotazioneRequest.getIdEvento(),
                        creaPrenotazioneRequest.getNumeroPartecipanti(),
                        LocalDateTime.now(),
                        LocalDateTime.now()))
                ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> modificaPrenotazione(Integer idPrenotazione, ModificaPrenotazioneRequest modificaPrenotazioneRequest) {
        return prenotazioneUseCasePortIn.updateById(idPrenotazione, modificaPrenotazioneRequest.getNumeroPartecipanti()) ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Prenotazione> cercaPrenotazioneByIdUtente(Integer idUtente) {
        return prenotazioneUseCasePortIn.findByUtente(idUtente).stream()
                .findFirst()
                .map(prenotazioneRecord -> ResponseEntity.ok(prenotazioneMapper.toPrenotazione(prenotazioneRecord)))
                .orElse(ResponseEntity.notFound().build());
    }
}
