package org.api.gateway.adapter.port.out.userService;

import lombok.RequiredArgsConstructor;
import org.api.gateway.adapter.client.UserServiceClient;
import org.api.gateway.adapter.client.dto.UtenteRuoloCreateRequest;
import org.api.gateway.adapter.client.dto.UtenteRuoloResponse;
import org.api.gateway.adapter.client.dto.UtenteRuoloUpdateRequest;
import org.api.gateway.domain.model.userService.UtenteRuoloRecord;
import org.api.gateway.domain.port.out.userService.UtenteRuoloUseCasePortOut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtenteRuoloUseCasePortOutImpl implements UtenteRuoloUseCasePortOut {

    private final UserServiceClient userServiceClient;

    @Override
    public Optional<UtenteRuoloRecord> findById(Integer id) {
        ResponseEntity<UtenteRuoloResponse> response = userServiceClient.getUtenteRuoloById(id);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? Optional.of(map(response.getBody()))
                : Optional.empty();
    }

    @Override
    public UtenteRuoloRecord save(UtenteRuoloRecord utenteRuolo) {
        ResponseEntity<UtenteRuoloResponse> response = userServiceClient.createUtenteRuolo(new UtenteRuoloCreateRequest(
                utenteRuolo.idUtente(),
                utenteRuolo.idRuolo()
        ));
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? map(response.getBody())
                : null;
    }

    @Override
    public List<UtenteRuoloRecord> findAll() {
        ResponseEntity<List<UtenteRuoloResponse>> response = userServiceClient.listUtenteRuoli();
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? response.getBody().stream().map(this::map).toList()
                : List.of();
    }

    @Override
    public Boolean deleteById(Integer id) {
        ResponseEntity<Void> response = userServiceClient.deleteUtenteRuolo(id);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Boolean updateById(Integer id, Long idUtente, Long idRuolo) {
        ResponseEntity<UtenteRuoloResponse> response = userServiceClient.updateUtenteRuolo(id, new UtenteRuoloUpdateRequest(idUtente.intValue(), idRuolo.intValue()));
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Optional<UtenteRuoloRecord> findByIdUtenteAndIdRuolo(Integer idUtente, Integer idRuolo) {
        ResponseEntity<UtenteRuoloResponse> response = userServiceClient.getUtenteRuoloByUtenteAndRuolo(idUtente, idRuolo);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? Optional.of(map(response.getBody()))
                : Optional.empty();
    }

    private UtenteRuoloRecord map(UtenteRuoloResponse response) {
        return new UtenteRuoloRecord(response.getIdUtenteRuolo(), response.getIdUtente(), response.getIdRuolo());
    }
}
