package org.api.gateway.adapter.client;

import org.openapitools.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${services.user-service.base-url}")
public interface UserServiceClient {

    @GetMapping("/utenti")
    ResponseEntity<List<UtenteResponse>> listUtenti();

    @GetMapping("/utenti/cercaByNomeUtenteLike")
    ResponseEntity<List<UtenteConRuoliResponse>> cercaUtentiLikeNome(@RequestParam("nomeUtente") String nomeUtente);

    @PostMapping("/utenti")
    ResponseEntity<UtenteResponse> createUtente(@RequestBody UtenteCreateRequest request);


    @PostMapping("/utenti")
    ResponseEntity<UtenteResponse> createUtente(@RequestBody RegistraRequest request);

    /*default ResponseEntity<UtenteResponse> createUtente(RegistraRequest request) {
        return createUtente(new UtenteCreateRequest(
                request.getNomeUtente(),
                request.getNome(),
                request.getCognome(),
                request.getEmail(),
                request.getPassword()
        ));
    }*/

    @GetMapping("/utenti/{idUtente}")
    ResponseEntity<UtenteConRuoliResponse> getUtenteById(@PathVariable("idUtente") Integer idUtente);

    @PutMapping("/utenti/{idUtente}")
    ResponseEntity<UtenteResponse> updateUtente(
            @PathVariable("idUtente") Integer idUtente,
            @RequestBody UtenteUpdateRequest request);

    @DeleteMapping("/utenti/{idUtente}")
    ResponseEntity<Void> deleteUtente(@PathVariable("idUtente") Integer idUtente);

    @GetMapping("/utenti/email/{email}")
    ResponseEntity<UtenteConRuoliResponse> getUtenteByEmail(@PathVariable("email") String email);

    @GetMapping("/utenti/nome/{nomeUtente}")
    ResponseEntity<UtenteConRuoliResponse> getUtenteByNomeUtente(@PathVariable("nomeUtente") String nomeUtente);

    @GetMapping("/utenti/credentials")
    ResponseEntity<UtenteResponse> getUtenteByUserNameAndPassword(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password);

    @GetMapping("/ruoli")
    ResponseEntity<List<RuoloResponse>> listRuoli();

    @PostMapping("/ruoli")
    ResponseEntity<RuoloResponse> createRuolo(@RequestBody RuoloCreateRequest request);

    @GetMapping("/ruoli/{idRuolo}")
    ResponseEntity<RuoloResponse> getRuoloById(@PathVariable("idRuolo") Integer idRuolo);

    @PutMapping("/ruoli/{idRuolo}")
    ResponseEntity<RuoloResponse> updateRuolo(
            @PathVariable("idRuolo") Integer idRuolo,
            @RequestBody RuoloUpdateRequest request);

    @DeleteMapping("/ruoli/{idRuolo}")
    ResponseEntity<Void> deleteRuolo(@PathVariable("idRuolo") Integer idRuolo);

    @GetMapping("/utente-ruoli")
    ResponseEntity<List<UtenteRuoloResponse>> listUtenteRuoli();

    @PostMapping("/utente-ruoli")
    ResponseEntity<UtenteRuoloResponse> createUtenteRuolo(@RequestBody UtenteRuoloBase request);

    @GetMapping("/utente-ruoli/cerca")
    ResponseEntity<UtenteRuoloResponse> getUtenteRuoloByUtenteAndRuolo(
            @RequestParam("idUtente") Integer idUtente,
            @RequestParam("idRuolo") Integer idRuolo);

    @GetMapping("/utente-ruoli/utente/{idUtente}")
    ResponseEntity<List<Integer>> getRuoliByUtente(
            @PathVariable("idUtente") Integer idUtente);

    @GetMapping("/utente-ruoli/{idUtenteRuolo}")
    ResponseEntity<UtenteRuoloResponse> getUtenteRuoloById(
            @PathVariable("idUtenteRuolo") Integer idUtenteRuolo);

    @PutMapping("/utente-ruoli/{idUtenteRuolo}")
    ResponseEntity<UtenteRuoloResponse> updateUtenteRuolo(
            @PathVariable("idUtenteRuolo") Integer idUtenteRuolo,
            @RequestBody UtenteRuoloBase request);

    @DeleteMapping("/utente-ruoli/{idUtenteRuolo}")
    ResponseEntity<Void> deleteUtenteRuolo(@PathVariable("idUtenteRuolo") Integer idUtenteRuolo);
}
