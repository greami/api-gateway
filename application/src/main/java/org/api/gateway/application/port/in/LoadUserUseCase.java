package org.api.gateway.application.port.in;

import org.api.gateway.application.model.AuthenticatedUser;

public interface LoadUserUseCase {
    AuthenticatedUser loadUserByUsername(String username);
}
