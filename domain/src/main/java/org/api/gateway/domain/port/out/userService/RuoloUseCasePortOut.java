package org.api.gateway.domain.port.out.userService;

import org.api.gateway.domain.model.userService.RuoloRecord;

import java.util.List;
import java.util.Optional;

public interface RuoloUseCasePortOut {
    Optional<RuoloRecord> findById(Integer id);
    RuoloRecord save(RuoloRecord ruolo);
    List<RuoloRecord> findAll();
    Boolean deleteById(Integer id);
    Boolean updateById(Integer id, String nome, String descrizione);
}
