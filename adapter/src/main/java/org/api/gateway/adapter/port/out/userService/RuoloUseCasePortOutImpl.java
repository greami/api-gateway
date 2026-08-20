package org.api.gateway.adapter.port.out.userService;

import lombok.RequiredArgsConstructor;
import org.api.gateway.adapter.client.UserServiceClient;
import org.api.gateway.adapter.client.dto.RuoloCreateRequest;
import org.api.gateway.adapter.client.dto.RuoloResponse;
import org.api.gateway.adapter.client.dto.RuoloUpdateRequest;
import org.api.gateway.domain.model.userService.RuoloRecord;
import org.api.gateway.domain.port.out.userService.RuoloUseCasePortOut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RuoloUseCasePortOutImpl implements RuoloUseCasePortOut {

    private final UserServiceClient userServiceClient;

    @Override
    public Optional<RuoloRecord> findById(Integer id) {
        ResponseEntity<RuoloResponse> response = userServiceClient.getRuoloById(id);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? Optional.of(map(response.getBody()))
                : Optional.empty();
    }

    @Override
    public RuoloRecord save(RuoloRecord ruolo) {
        ResponseEntity<RuoloResponse> response = userServiceClient.createRuolo(new RuoloCreateRequest(
                ruolo.id(),
                ruolo.nome(),
                ruolo.descrizione()
        ));
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? map(response.getBody())
                : null;
    }

    @Override
    public List<RuoloRecord> findAll() {
        ResponseEntity<List<RuoloResponse>> response = userServiceClient.listRuoli();
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? response.getBody().stream().map(this::map).toList()
                : List.of();
    }

    @Override
    public Boolean deleteById(Integer id) {
        ResponseEntity<Void> response = userServiceClient.deleteRuolo(id);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Boolean updateById(Integer id, String nome, String descrizione) {
        ResponseEntity<RuoloResponse> response = userServiceClient.updateRuolo(id, new RuoloUpdateRequest(id, nome, descrizione));
        return response.getStatusCode().is2xxSuccessful();
    }

    private RuoloRecord map(RuoloResponse response) {
        return new RuoloRecord(response.getIdRuolo(), response.getNome(), response.getDescrizione());
    }
}
