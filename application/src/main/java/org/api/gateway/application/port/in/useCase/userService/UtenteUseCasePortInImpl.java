package org.api.gateway.application.port.in.useCase.userService;

import lombok.RequiredArgsConstructor;
import org.api.gateway.domain.model.userService.UtenteRecord;
import org.api.gateway.domain.port.in.userService.UtenteUseCasePortIn;
import org.api.gateway.domain.port.out.userService.UtenteUseCasePortOut;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtenteUseCasePortInImpl implements UtenteUseCasePortIn {

    private final UtenteUseCasePortOut utenteUseCasePortOut;

    @Override
    public Optional<UtenteRecord> findById(Integer id) {
        return utenteUseCasePortOut.findById(id);
    }

    @Override
    public UtenteRecord save(UtenteRecord utente) {
        return utenteUseCasePortOut.save(utente);
    }

    @Override
    public List<UtenteRecord> findAll() {
        return utenteUseCasePortOut.findAll();
    }

    @Override
    public Boolean deleteById(Integer id) {
        return utenteUseCasePortOut.deleteById(id);
    }

    @Override
    public Boolean updateById(Integer id, String nomeUtente, String nome, String cognome, String email, String password) {
        return utenteUseCasePortOut.updateById(id, nomeUtente, nome, cognome, email, password);
    }

    @Override
    public Optional<UtenteRecord> findByNomeUtente(String nomeUtente) {
        return utenteUseCasePortOut.findByNomeUtente(nomeUtente);
    }

    @Override
    public List<UtenteRecord> findByNomeUtenteLike(String nomeUtente) {
        return utenteUseCasePortOut.findByNomeUtenteLike(nomeUtente);
    }

    @Override
    public Optional<UtenteRecord> findByNomeUtenteAndPassword(String nomeUtente, String password) {
        return utenteUseCasePortOut.findByNomeUtenteAndPassword(nomeUtente, password);
    }

    @Override
    public Optional<UtenteRecord> findByEmail(String email) {
        return utenteUseCasePortOut.findByEmail(email);
    }
}
