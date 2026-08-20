package org.api.gateway.application.port.in.useCase;

import lombok.RequiredArgsConstructor;
import org.api.gateway.domain.model.userService.PrenotazioneRecord;
import org.api.gateway.domain.port.in.PrenotazioneUseCasePortIn;
import org.api.gateway.domain.port.out.PrenotazioneRepositoryPortOut;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PrenotazioneUseCaseImpl implements PrenotazioneUseCasePortIn
{
    private final PrenotazioneRepositoryPortOut prenotazioneRepository;

    @Override
    public Optional<PrenotazioneRecord> findById(Integer id) {
        return prenotazioneRepository.findById(id);
    }

    @Override
    public Boolean save(PrenotazioneRecord prenotazione) {
        return prenotazioneRepository.save(prenotazione);
    }

    @Override
    public List<PrenotazioneRecord> findAll() {
        return prenotazioneRepository.findAll();
    }

    @Override
    public Boolean deleteById(Integer id) {
        return prenotazioneRepository.deleteById(id);
    }

    @Override
    public Boolean updateById(Integer id, Integer numeroPosti) {
        return prenotazioneRepository.updateById(id, numeroPosti);
    }
    @Override
    public List<PrenotazioneRecord> findByEvento(Integer id) {
        return prenotazioneRepository.findByEvento(id);
    }

    @Override
    public List<PrenotazioneRecord> findByUtente(Integer id) {
        return prenotazioneRepository.findByUtente(id);
    }
}
