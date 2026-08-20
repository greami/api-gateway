package org.api.gateway.application.port.in.useCase.userService;

import lombok.RequiredArgsConstructor;
import org.api.gateway.domain.model.userService.RuoloRecord;
import org.api.gateway.domain.port.in.userService.RuoloUseCasePortIn;
import org.api.gateway.domain.port.out.userService.RuoloUseCasePortOut;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RuoloUseCasePortInImpl implements RuoloUseCasePortIn {

    private final RuoloUseCasePortOut ruoloUseCasePortOut;

    @Override
    public Optional<RuoloRecord> findById(Integer id) {
        return ruoloUseCasePortOut.findById(id);
    }

    @Override
    public RuoloRecord save(RuoloRecord ruolo) {
        return ruoloUseCasePortOut.save(ruolo);
    }

    @Override
    public List<RuoloRecord> findAll() {
        return ruoloUseCasePortOut.findAll();
    }

    @Override
    public Boolean deleteById(Integer id) {
        return  ruoloUseCasePortOut.deleteById(id);
    }

    @Override
    public Boolean updateById(Integer id, String nome, String descrizione) {
        return ruoloUseCasePortOut.updateById(id, nome, descrizione);
    }
}
