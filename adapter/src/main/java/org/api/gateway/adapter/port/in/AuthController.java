package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.port.out.GatewayResponseMapper;
import org.api.gateway.application.port.in.AuthenticationUseCase;
import org.openapitools.api.AuthApi;
import org.openapitools.model.AuthRequest;
import org.openapitools.model.AuthResponse;
import org.openapitools.model.RecuperaCredenzialiRequest;
import org.openapitools.model.RegistraRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {
    private final AuthenticationUseCase authenticationUseCase;

    public AuthController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @Override
    public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
        AuthResponse response = authenticationUseCase.login(authRequest);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(response.getUsername())
                .password("")
                .authorities(response.getRoles().toArray(String[]::new))
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                )
        );
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> recuperaCredenziali(RecuperaCredenzialiRequest recuperaCredenzialiRequest) {
        return GatewayResponseMapper.toResponseEntity(
                authenticationUseCase.recuperaCredenziali(recuperaCredenzialiRequest));
    }

    @Override
    public ResponseEntity<Void> registra(RegistraRequest registraRequest) {
        return GatewayResponseMapper.toResponseEntity(authenticationUseCase.registra(registraRequest));
    }
}
