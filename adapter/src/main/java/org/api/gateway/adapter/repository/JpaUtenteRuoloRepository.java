package org.api.gateway.adapter.repository;

import org.api.gateway.adapter.repository.entity.UtenteRuoloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUtenteRuoloRepository extends JpaRepository<UtenteRuoloEntity, Long>
{
}
