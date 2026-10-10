package org.api.gateway.application.port.out;

public interface JwtTokenPort {
    String generateToken(String username);
}
