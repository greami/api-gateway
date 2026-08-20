package org.api.gateway.application.port.in.Credential;

import org.api.gateway.domain.model.userService.RuoloRecord;
import org.api.gateway.domain.model.userService.UtenteRecord;
import org.api.gateway.domain.model.userService.UtenteRuoloRecord;
import org.api.gateway.domain.port.in.CredenzialiRequestPortIn;

import java.util.List;
import java.util.Optional;

public class CredentialRequestPortInImpl implements CredenzialiRequestPortIn {



    @Override
    public Boolean existsUtenteById(Integer id) {
        return null;
    }

    @Override
    public Optional<UtenteRecord> findUtenteById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Boolean saveUtente(UtenteRecord utente) {
        return null;
    }

    @Override
    public List<UtenteRecord> findAll() {
        return List.of();
    }

    @Override
    public Boolean deleteUtenteById(Integer id) {
        return null;
    }

    @Override
    public Boolean updateUtenteById(Integer id, String nome, String cognome, String email, String password) {
        return null;
    }

    @Override
    public List<UtenteRuoloRecord> getRuolofromUtente() {
        return List.of();
    }

    @Override
    public Boolean saveRuoloUtente(UtenteRuoloRecord utenteRuoloRecord) {
        return null;
    }

    @Override
    public boolean updateRuoloUtente(Integer id, Integer idRuolo) {
        return false;
    }

    @Override
    public boolean deleteRuoloUtente(Integer id) {
        return false;
    }

    @Override
    public List<UtenteRuoloRecord> getRuolifromUtente() {
        return List.of();
    }

    @Override
    public Boolean saveRuoloUtente(RuoloRecord ruoloRecord) {
        return null;
    }

    @Override
    public boolean updateRuolo(Integer id, String nome, String descrizione) {
        return false;
    }

    @Override
    public boolean deleteRuolo(Integer id) {
        return false;
    }
}
