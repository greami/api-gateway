package org.api.gateway.application.model;

import java.util.List;
import java.util.Objects;

public record AuthenticatedUser(String username, String password, List<String> authorities) {
    public AuthenticatedUser {
        Objects.requireNonNull(username, "username");
        authorities = List.copyOf(Objects.requireNonNull(authorities, "authorities"));
    }
}
