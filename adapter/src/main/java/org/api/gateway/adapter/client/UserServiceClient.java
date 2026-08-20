package org.api.gateway.adapter.client;

import org.api.gateway.adapter.client.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${services.user-service.base-url}")
public interface UserServiceClient {

    // ============ UTENTI ENDPOINTS ============

    @GetMapping("/utenti")
    ResponseEntity<List<UtenteResponse>> listUtenti();

    @PostMapping("/utenti")
    ResponseEntity<UtenteResponse> createUtente(@RequestBody UtenteCreateRequest request);

    @GetMapping("/utenti/{idUtente}")
    ResponseEntity<UtenteResponse> getUtenteById(@PathVariable("idUtente") Integer idUtente);

    @PutMapping("/utenti/{idUtente}")
    ResponseEntity<UtenteResponse> updateUtente(@PathVariable("idUtente") Integer idUtente, @RequestBody UtenteUpdateRequest request);

    @DeleteMapping("/utenti/{idUtente}")
    ResponseEntity<Void> deleteUtente(@PathVariable("idUtente") Integer idUtente);

    @GetMapping("/utenti/email/{email}")
    ResponseEntity<UtenteResponse> getUtenteByEmail(@PathVariable("email") String email);

    @GetMapping("/utenti/nome/{nomeUtente}")
    ResponseEntity<UtenteResponse> getUtenteByNomeUtente(@PathVariable("nomeUtente") String nomeUtente);

    @GetMapping("/utenti/credentials")
    ResponseEntity<UtenteResponse> getUtenteByCredential(
            @RequestParam("userName") String userName,
            @RequestParam("password") String password);

    // ============ RUOLI ENDPOINTS ============

    @GetMapping("/ruoli")
    ResponseEntity<List<RuoloResponse>> listRuoli();

    @PostMapping("/ruoli")
    ResponseEntity<RuoloResponse> createRuolo(@RequestBody RuoloCreateRequest request);

    @GetMapping("/ruoli/{idRuolo}")
    ResponseEntity<RuoloResponse> getRuoloById(@PathVariable("idRuolo") Integer idRuolo);

    @PutMapping("/ruoli/{idRuolo}")
    ResponseEntity<RuoloResponse> updateRuolo(@PathVariable("idRuolo") Integer idRuolo, @RequestBody RuoloUpdateRequest request);

    @DeleteMapping("/ruoli/{idRuolo}")
    ResponseEntity<Void> deleteRuolo(@PathVariable("idRuolo") Integer idRuolo);

    // ============ UTENTE-RUOLI ENDPOINTS ============

    @GetMapping("/utente-ruoli")
    ResponseEntity<List<UtenteRuoloResponse>> listUtenteRuoli();

    @PostMapping("/utente-ruoli")
    ResponseEntity<UtenteRuoloResponse> createUtenteRuolo(@RequestBody UtenteRuoloCreateRequest request);

    @GetMapping("/utente-ruoli/cerca")
    ResponseEntity<UtenteRuoloResponse> getUtenteRuoloByUtenteAndRuolo(
            @RequestParam("idUtente") Integer idUtente,
            @RequestParam("idRuolo") Integer idRuolo);

    @GetMapping("/utente-ruoli/{idUtenteRuolo}")
    ResponseEntity<UtenteRuoloResponse> getUtenteRuoloById(@PathVariable("idUtenteRuolo") Integer idUtenteRuolo);

    @PutMapping("/utente-ruoli/{idUtenteRuolo}")
    ResponseEntity<UtenteRuoloResponse> updateUtenteRuolo(@PathVariable("idUtenteRuolo") Integer idUtenteRuolo, @RequestBody UtenteRuoloUpdateRequest request);

    @DeleteMapping("/utente-ruoli/{idUtenteRuolo}")
    ResponseEntity<Void> deleteUtenteRuolo(@PathVariable("idUtenteRuolo") Integer idUtenteRuolo);
}
