package org.api.gateway.adapter.repository;

import org.api.gateway.adapter.repository.entity.PrenotazioneEntity;

import java.util.List;

public interface JpaPrenotazioneRepository extends org.springframework.data.jpa.repository.JpaRepository<PrenotazioneEntity, Integer> {

    List<PrenotazioneEntity> findByIdEvento(Integer idEvento);
    List<PrenotazioneEntity> findByIdUtente(Integer idEvento);
}
