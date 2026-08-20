package org.api.gateway.adapter.repository;


import org.api.gateway.adapter.repository.entity.RuoloEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRuoloRepository extends JpaRepository<RuoloEntity, Integer>
{
}
