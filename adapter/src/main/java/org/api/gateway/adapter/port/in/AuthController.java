package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.client.UserServiceClient;
import org.api.gateway.adapter.security.CustomUserDetailsService;
import org.api.gateway.adapter.security.JwtService;
import feign.FeignException;
import org.openapitools.api.AuthApi;
import org.openapitools.model.AuthRequest;
import org.openapitools.model.AuthResponse;
import org.openapitools.model.RecuperaCredenzialiRequest;
import org.openapitools.model.RegistraRequest;
import org.openapitools.model.UtenteConRuoliResponse;
import org.openapitools.model.UtenteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final UserServiceClient userServiceClient;


    public AuthController(CustomUserDetailsService customUserDetailsService, JwtService jwtService, UserServiceClient userServiceClient) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
        UserDetails userDetails = customUserDetailsService.loadByUserNameAndPassword(
                authRequest.getUsername(),
                authRequest.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                )
        );
        String token = jwtService.generateToken(userDetails);

        ResponseEntity<UtenteConRuoliResponse> userResponse = userServiceClient.getUtenteByNomeUtente(authRequest.getUsername());
        UtenteConRuoliResponse utente = userResponse.getBody();
        if (utente == null || utente.getIdUtente() == null) {
            throw new IllegalStateException("Authenticated user profile not found");
        }

        return ResponseEntity.ok(new AuthResponse(
                token,
                "Bearer",
                authRequest.getUsername(),
                userDetails.getAuthorities().stream()
                        .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                        .toList(),
                utente.getIdUtente()
        ));

    }

    @Override
    public ResponseEntity<Void> recuperaCredenziali(RecuperaCredenzialiRequest recuperaCredenzialiRequest) {
        return null;
    }

    @Override
    public ResponseEntity<Void> registra(RegistraRequest registraRequest) {
        try {
            ResponseEntity<UtenteResponse> response = userServiceClient.createUtente(registraRequest);
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.status(response.getStatusCode()).build();
        } catch (FeignException.BadRequest exception) {
            return ResponseEntity.badRequest().build();
        }
    }


}
