package org.api.gateway.adapter.port.out;

import org.api.gateway.adapter.repository.JpaRuoloRepository;
import org.api.gateway.adapter.repository.JpaUtenteRuoloRepository;
import org.api.gateway.adapter.repository.JpaUtenteRepository;
import org.api.gateway.domain.model.userService.RuoloRecord;
import org.api.gateway.domain.model.userService.UtenteRecord;
import org.api.gateway.domain.model.userService.UtenteRuoloRecord;
import org.api.gateway.domain.port.out.UserServicePortOut;

import java.util.List;
import java.util.Optional;

public class UserServicePortOutImpl implements UserServicePortOut
{
    private final JpaRuoloRepository jpaRuoloRepository;
    private final JpaUtenteRuoloRepository jpaUtenteRuoloRepository;
    private final JpaUtenteRepository jpaUtenteRepository;

    public UserServicePortOutImpl(JpaRuoloRepository jpaRuoloRepository, JpaUtenteRuoloRepository jpaUtenteRuoloRepository, JpaUtenteRepository jpaUtenteRepository) {
        this.jpaRuoloRepository = jpaRuoloRepository;
        this.jpaUtenteRuoloRepository = jpaUtenteRuoloRepository;
        this.jpaUtenteRepository = jpaUtenteRepository;
    }


    @Override
    public Boolean existsUtenteById(Integer id) {
        return null; //jpaUtenteRepository.existsById(id);
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
