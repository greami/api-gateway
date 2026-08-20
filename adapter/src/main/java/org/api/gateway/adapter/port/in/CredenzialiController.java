package org.api.gateway.adapter.port.in;

import org.api.gateway.domain.model.userService.UtenteRuoloRecord;
import org.openapitools.api.RuoliApi;
import org.openapitools.api.UtenteRuoliApi;
import org.openapitools.api.UtentiApi;
import org.openapitools.model.*;
import org.api.gateway.adapter.mapper.CredenzialiMapper;
import org.api.gateway.domain.port.in.userService.RuoloUseCasePortIn;
import org.api.gateway.domain.port.in.userService.UtenteRuoloUseCasePortIn;
import org.api.gateway.domain.port.in.userService.UtenteUseCasePortIn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CredenzialiController implements RuoliApi, UtenteRuoliApi, UtentiApi {

    private final UtenteUseCasePortIn utenteUseCasePortIn;
    private final RuoloUseCasePortIn ruoloUseCasePortIn;
    private final UtenteRuoloUseCasePortIn utenteRuoloUseCasePortIn;
    private final CredenzialiMapper credenzialiMapper;

    public CredenzialiController(UtenteUseCasePortIn utenteUseCasePortIn,
                                 RuoloUseCasePortIn ruoloUseCasePortIn,
                                 UtenteRuoloUseCasePortIn utenteRuoloUseCasePortIn,
                                 CredenzialiMapper credenzialiMapper) {
        this.utenteUseCasePortIn = utenteUseCasePortIn;
        this.ruoloUseCasePortIn = ruoloUseCasePortIn;
        this.utenteRuoloUseCasePortIn = utenteRuoloUseCasePortIn;
        this.credenzialiMapper = credenzialiMapper;
    }

    @Override
    public ResponseEntity<List<UtenteResponse>> listUtenti() {
        var utenti = utenteUseCasePortIn.findAll().stream()
                .map(credenzialiMapper::toUtenteResponse)
                .toList();
        return utenti.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(utenti);
    }

    @Override
    public ResponseEntity<UtenteResponse> getUtenteById(Integer idUtente) {
        return utenteUseCasePortIn.findById(idUtente)
                .map(utenteRecord -> ResponseEntity.ok(credenzialiMapper.toUtenteResponse(utenteRecord)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UtenteResponse> getUtenteByEmail(String email) {
        return utenteUseCasePortIn.findByEmail(email)
                .map(utenteRecord -> ResponseEntity.ok(credenzialiMapper.toUtenteResponse(utenteRecord)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UtenteResponse> getUtenteByNomeUtente(String nomeUtente) {
        return utenteUseCasePortIn.findByNomeUtente(nomeUtente)
                .map(utenteRecord -> ResponseEntity.ok(credenzialiMapper.toUtenteResponse(utenteRecord)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UtenteResponse> getUtenteByUserNameAndPassword(String userName, String password) {
        return utenteUseCasePortIn.findByNomeUtenteAndPassword(userName, password)
                .map(utenteRecord -> ResponseEntity.ok(credenzialiMapper.toUtenteResponse(utenteRecord)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UtenteResponse> createUtente(UtenteCreateRequest utenteCreateRequest) {
        var created = utenteUseCasePortIn.save(credenzialiMapper.toUtenteRecord(utenteCreateRequest));
        return created != null
                ? ResponseEntity.ok(credenzialiMapper.toUtenteResponse(created))
                : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<UtenteResponse> updateUtente(Integer idUtente, UtenteUpdateRequest utenteUpdateRequest) {
        return utenteUseCasePortIn.updateById(
                        idUtente,
                        utenteUpdateRequest.getNomeUtente(),
                        utenteUpdateRequest.getNome(),
                        utenteUpdateRequest.getCognome(),
                        utenteUpdateRequest.getEmail(),
                        utenteUpdateRequest.getPassword())
                ? utenteUseCasePortIn.findById(idUtente)
                        .map(utenteRecord -> ResponseEntity.ok(credenzialiMapper.toUtenteResponse(utenteRecord)))
                        .orElse(ResponseEntity.notFound().build())
                : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteUtente(Integer idUtente) {
        return utenteUseCasePortIn.deleteById(idUtente) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<RuoloResponse>> listRuoli() {
        var ruoli = ruoloUseCasePortIn.findAll().stream()
                .map(credenzialiMapper::toRuoloResponse)
                .toList();
        return ruoli.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(ruoli);
    }

    @Override
    public ResponseEntity<RuoloResponse> getRuoloById(Integer idRuolo) {
        return ruoloUseCasePortIn.findById(idRuolo)
                .map(ruoloRecord -> ResponseEntity.ok(credenzialiMapper.toRuoloResponse(ruoloRecord)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<RuoloResponse> createRuolo(RuoloCreateRequest ruoloCreateRequest) {
        var created = ruoloUseCasePortIn.save(credenzialiMapper.toRuoloRecord(ruoloCreateRequest));
        return created != null
                ? ResponseEntity.ok(credenzialiMapper.toRuoloResponse(created))
                : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<RuoloResponse> updateRuolo(Integer idRuolo, RuoloUpdateRequest ruoloUpdateRequest) {
        return ruoloUseCasePortIn.updateById(idRuolo, ruoloUpdateRequest.getNome(), ruoloUpdateRequest.getDescrizione())
                ? ruoloUseCasePortIn.findById(idRuolo)
                        .map(ruoloRecord -> ResponseEntity.ok(credenzialiMapper.toRuoloResponse(ruoloRecord)))
                        .orElse(ResponseEntity.notFound().build())
                : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteRuolo(Integer idRuolo) {
        return ruoloUseCasePortIn.deleteById(idRuolo) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<List<UtenteRuoloResponse>> listUtenteRuoli() {
        var utenteRuoli = utenteRuoloUseCasePortIn.findAll().stream()
                .map(credenzialiMapper::toUtenteRuoloResponse)
                .toList();
        return utenteRuoli.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(utenteRuoli);
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> getUtenteRuoloById(Integer idUtenteRuolo) {
        return utenteRuoloUseCasePortIn.findById(idUtenteRuolo)
                .map(utenteRuoloRecord -> ResponseEntity.ok(credenzialiMapper.toUtenteRuoloResponse(utenteRuoloRecord)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> getUtenteRuoloByUtenteAndRuolo(Integer idUtente, Integer idRuolo) {
        return utenteRuoloUseCasePortIn.findByIdUtenteAndIdRuolo(idUtente, idRuolo)
                .map(utenteRuoloRecord -> ResponseEntity.ok(credenzialiMapper.toUtenteRuoloResponse(utenteRuoloRecord)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> createUtenteRuolo(UtenteRuoloBase utenteRuoloBase) {
        var created = utenteRuoloUseCasePortIn.save(new UtenteRuoloRecord(null, utenteRuoloBase.getIdUtente(), utenteRuoloBase.getIdRuolo()));
        return created != null
                ? ResponseEntity.ok(credenzialiMapper.toUtenteRuoloResponse(created))
                : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<UtenteRuoloResponse> updateUtenteRuolo(Integer idUtenteRuolo, UtenteRuoloBase utenteRuoloBase) {
        return utenteRuoloUseCasePortIn.updateById(
                        idUtenteRuolo,
                        Long.valueOf(utenteRuoloBase.getIdUtente()),
                        Long.valueOf(utenteRuoloBase.getIdRuolo()))
                ? utenteRuoloUseCasePortIn.findById(idUtenteRuolo)
                        .map(utenteRuoloRecord -> ResponseEntity.ok(credenzialiMapper.toUtenteRuoloResponse(utenteRuoloRecord)))
                        .orElse(ResponseEntity.notFound().build())
                : ResponseEntity.badRequest().build();
    }

    @Override
    public ResponseEntity<Void> deleteUtenteRuolo(Integer idUtenteRuolo) {
        return utenteRuoloUseCasePortIn.deleteById(idUtenteRuolo) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
