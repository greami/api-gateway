package org.api.gateway.adapter.repository;

import org.api.gateway.adapter.repository.entity.UtenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUtenteRepository extends JpaRepository<UtenteEntity, Long>
{
    Optional<UtenteEntity> findByNomeUtente(String nomeUtente);

    Optional<UtenteEntity> findByEmail(String email);
}
