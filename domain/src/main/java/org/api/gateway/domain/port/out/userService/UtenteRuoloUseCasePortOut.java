package org.api.gateway.domain.port.out.userService;


import org.api.gateway.domain.model.userService.UtenteRuoloRecord;

import java.util.List;
import java.util.Optional;

public interface UtenteRuoloUseCasePortOut {
    Optional<UtenteRuoloRecord> findById(Integer id);
    UtenteRuoloRecord save(UtenteRuoloRecord utenteRuolo);
    List<UtenteRuoloRecord> findAll();
    Boolean deleteById(Integer id);
    Boolean updateById(Integer id, Long idUtente, Long idRuolo);
    Optional<UtenteRuoloRecord> findByIdUtenteAndIdRuolo(Integer idUtente, Integer idRuolo);
}
