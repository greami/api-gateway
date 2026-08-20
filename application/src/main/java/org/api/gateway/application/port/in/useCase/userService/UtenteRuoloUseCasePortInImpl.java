package org.api.gateway.application.port.in.useCase.userService;

import org.api.gateway.domain.model.userService.UtenteRuoloRecord;
import org.api.gateway.domain.port.in.userService.UtenteRuoloUseCasePortIn;
import org.api.gateway.domain.port.out.userService.UtenteRuoloUseCasePortOut;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UtenteRuoloUseCasePortInImpl implements UtenteRuoloUseCasePortIn {

    private final UtenteRuoloUseCasePortOut utenteRuoloUseCasePortOut;

    public UtenteRuoloUseCasePortInImpl(UtenteRuoloUseCasePortOut utenteRuoloUseCasePortOut) {
        this.utenteRuoloUseCasePortOut = utenteRuoloUseCasePortOut;
    }

    @Override
    public Optional<UtenteRuoloRecord> findById(Integer id) {
        return utenteRuoloUseCasePortOut.findById(id);
    }

    @Override
    public UtenteRuoloRecord save(UtenteRuoloRecord utenteRuolo) {
        return utenteRuoloUseCasePortOut.save(utenteRuolo);
    }

    @Override
    public List<UtenteRuoloRecord> findAll() {
        return utenteRuoloUseCasePortOut.findAll();
    }

    @Override
    public Boolean deleteById(Integer id) {
        return utenteRuoloUseCasePortOut.deleteById(id);
    }

    @Override
    public Boolean updateById(Integer id, Long idUtente, Long idRuolo) {
        return utenteRuoloUseCasePortOut.updateById(id, idUtente, idRuolo);
    }

    @Override
    public Optional<UtenteRuoloRecord> findByIdUtenteAndIdRuolo(Integer idUtente, Integer idRuolo) {
        return utenteRuoloUseCasePortOut.findByIdUtenteAndIdRuolo(idUtente, idRuolo);
    }
}
