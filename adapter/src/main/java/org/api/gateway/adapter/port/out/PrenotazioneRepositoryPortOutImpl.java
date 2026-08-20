package org.api.gateway.adapter.port.out;

import org.api.gateway.adapter.mapper.PrenotazioneMapper;
import org.api.gateway.adapter.repository.JpaPrenotazioneRepository;
import org.api.gateway.adapter.repository.entity.PrenotazioneEntity;
import org.api.gateway.domain.model.userService.PrenotazioneRecord;
import org.api.gateway.domain.port.out.PrenotazioneRepositoryPortOut;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PrenotazioneRepositoryPortOutImpl implements PrenotazioneRepositoryPortOut {

    private final JpaPrenotazioneRepository jpaPrenotazioneRepository;
    private final PrenotazioneMapper prenotazioneMapper;

    public PrenotazioneRepositoryPortOutImpl(JpaPrenotazioneRepository jpaPrenotazioneRepository,
                                             PrenotazioneMapper prenotazioneMapper) {
        this.jpaPrenotazioneRepository = jpaPrenotazioneRepository;
        this.prenotazioneMapper = prenotazioneMapper;
    }

    @Override
    public Optional<PrenotazioneRecord> findById(Integer id) {
        return jpaPrenotazioneRepository.findById(id)
                .map(entity -> new PrenotazioneRecord(
                        entity.getId(),
                        entity.getIdUtente(),
                        entity.getIdEvento(),
                        entity.getNumeroPosti(),
                        entity.getDataPrenotazione(),
                        entity.getDataModifica()
                ));
    }

    @Override
    public Boolean save(PrenotazioneRecord prenotazione) {
        return jpaPrenotazioneRepository.save(prenotazioneMapper.toPrenotazioneEntity(prenotazione)) != null;
    }

    @Override
    public List<PrenotazioneRecord> findAll() {
        return jpaPrenotazioneRepository.findAll().stream()
                .map(entity -> new PrenotazioneRecord(
                        entity.getId(),
                        entity.getIdUtente(),
                        entity.getIdEvento(),
                        entity.getNumeroPosti(),
                        entity.getDataPrenotazione(),
                        entity.getDataModifica()
                ))
                .toList();
    }

    @Override
    public Boolean deleteById(Integer id) {
        if (!jpaPrenotazioneRepository.existsById(id)) {
            return false;
        }
        jpaPrenotazioneRepository.deleteById(id);
        return true;
    }

    @Override
    public Boolean updateById(Integer id, Integer numeroPosti) {
        return jpaPrenotazioneRepository.findById(id)
                .map(existing -> {
                    PrenotazioneEntity updated = new PrenotazioneEntity(
                            existing.getId(),
                            existing.getIdUtente(),
                            existing.getIdEvento(),
                            numeroPosti,
                            existing.getDataPrenotazione(),
                            java.time.LocalDateTime.now()
                    );
                    return jpaPrenotazioneRepository.save(updated) != null;
                })
                .orElse(false);
    }


    @Override
    public List<PrenotazioneRecord> findByEvento(Integer id) {
        return jpaPrenotazioneRepository.findByIdEvento(id).stream().map(
                entity -> new PrenotazioneRecord(
                        entity.getId(),
                        entity.getIdUtente(),
                        entity.getIdEvento(),
                        entity.getNumeroPosti(),
                        entity.getDataPrenotazione(),
                        entity.getDataModifica()
                )
        ).toList();
    }

    @Override
    public List<PrenotazioneRecord> findByUtente(Integer id) {
        return jpaPrenotazioneRepository.findByIdUtente(id).stream().map(
                entity -> new PrenotazioneRecord(
                        entity.getId(),
                        entity.getIdUtente(),
                        entity.getIdEvento(),
                        entity.getNumeroPosti(),
                        entity.getDataPrenotazione(),
                        entity.getDataModifica()
                )
        ).toList();
    }
}
