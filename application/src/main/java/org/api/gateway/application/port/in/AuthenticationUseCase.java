package org.api.gateway.application.port.in;

import org.api.gateway.application.model.GatewayResponse;
import org.openapitools.model.AuthRequest;
import org.openapitools.model.AuthResponse;
import org.openapitools.model.RecuperaCredenzialiRequest;
import org.openapitools.model.RegistraRequest;

public interface AuthenticationUseCase {
    AuthResponse login(AuthRequest request);

    GatewayResponse<Void> recuperaCredenziali(RecuperaCredenzialiRequest request);

    GatewayResponse<Void> registra(RegistraRequest request);
}
