package org.api.gateway.domain.port.out;

import org.api.gateway.domain.model.userService.PrenotazioneRecord;

import java.util.List;
import java.util.Optional;

public interface PrenotazioneRepositoryPortOut {
    Optional<PrenotazioneRecord> findById(Integer id);
    Boolean save(PrenotazioneRecord prenotazione);
    List<PrenotazioneRecord> findAll();
    Boolean deleteById(Integer id);
    Boolean updateById(Integer id, Integer numeroPosti);
    List<PrenotazioneRecord> findByEvento(Integer id);
    List<PrenotazioneRecord> findByUtente(Integer id);



}
