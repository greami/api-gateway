package org.api.gateway.domain.port.out.userService;

import org.api.gateway.domain.model.userService.UtenteRecord;
import java.util.List;
import java.util.Optional;

public interface UtenteUseCasePortOut {
    Optional<UtenteRecord> findById(Integer id);
    UtenteRecord save(UtenteRecord utente);
    List<UtenteRecord> findAll();
    Boolean deleteById(Integer id);
    Boolean updateById(Integer id, String nomeUtente, String nome, String cognome, String email, String password);
    Optional<UtenteRecord> findByNomeUtente(String nomeUtente);
    Optional<UtenteRecord> findByNomeUtenteAndPassword(String nomeUtente, String password);
    Optional<UtenteRecord> findByEmail(String email);
}
