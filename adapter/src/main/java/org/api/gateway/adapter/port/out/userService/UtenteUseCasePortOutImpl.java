package org.api.gateway.adapter.port.out.userService;

import lombok.RequiredArgsConstructor;
import org.api.gateway.adapter.client.UserServiceClient;
import org.api.gateway.domain.model.userService.UtenteRecord;
import org.api.gateway.domain.port.out.userService.UtenteUseCasePortOut;
import org.openapitools.model.UtenteCreateRequest;
import org.openapitools.model.UtenteResponse;
import org.openapitools.model.UtenteUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtenteUseCasePortOutImpl implements UtenteUseCasePortOut {

    private final UserServiceClient userServiceClient;

    @Override
    public Optional<UtenteRecord> findById(Integer id) {
        ResponseEntity<UtenteResponse> response = userServiceClient.getUtenteById(id);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? Optional.of(map(response.getBody()))
                : Optional.empty();
    }

    @Override
    public UtenteRecord save(UtenteRecord utente) {
        ResponseEntity<UtenteResponse> response = userServiceClient.createUtente(new UtenteCreateRequest(
                utente.nomeUtente(),
                utente.nome(),
                utente.cognome(),
                utente.email(),
                utente.password()
        ));

        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? map(response.getBody())
                : null;
    }

    @Override
    public List<UtenteRecord> findAll() {
        ResponseEntity<List<UtenteResponse>> response = userServiceClient.listUtenti();
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? response.getBody().stream().map(this::map).toList()
                : List.of();
    }

    @Override
    public Boolean deleteById(Integer id) {
        ResponseEntity<Void> response = userServiceClient.deleteUtente(id);
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Boolean updateById(Integer id, String nomeUtente, String nome, String cognome, String email, String password) {
        ResponseEntity<UtenteResponse> response = userServiceClient.updateUtente(id, new UtenteUpdateRequest(nomeUtente, nome, cognome, email, password));
        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public Optional<UtenteRecord> findByNomeUtente(String nomeUtente) {
        ResponseEntity<UtenteResponse> response = userServiceClient.getUtenteByNomeUtente(nomeUtente);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? Optional.of(map(response.getBody()))
                : Optional.empty();
    }

    @Override
    public List<UtenteRecord> findByNomeUtenteLike(String nomeUtente) {
        ResponseEntity<List<UtenteResponse>> response = userServiceClient.cercaUtentiLikeNome(nomeUtente);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? response.getBody().stream().map(this::map).toList()
                : List.of();
    }

    @Override
    public Optional<UtenteRecord> findByNomeUtenteAndPassword(String nomeUtente, String password) {
        ResponseEntity<UtenteResponse> response = userServiceClient.getUtenteByUserNameAndPassword(nomeUtente, password);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? Optional.of(mapWithPassword(response.getBody(), password))
                : Optional.empty();
    }

    @Override
    public Optional<UtenteRecord> findByEmail(String email) {
        ResponseEntity<UtenteResponse> response = userServiceClient.getUtenteByEmail(email);
        return response.getStatusCode().is2xxSuccessful() && response.getBody() != null
                ? Optional.of(map(response.getBody()))
                : Optional.empty();
    }

    private UtenteRecord map(UtenteResponse response) {
        return new UtenteRecord(
                response.getIdUtente(),
                response.getNomeUtente(),
                response.getNome(),
                response.getCognome(),
                response.getEmail(),
                null
        );
    }

    private UtenteRecord mapWithPassword(UtenteResponse response, String password) {
        return new UtenteRecord(
                response.getIdUtente(),
                response.getNomeUtente(),
                response.getNome(),
                response.getCognome(),
                response.getEmail(),
                password
        );
    }
}
