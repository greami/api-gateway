package org.api.gateway.domain.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
}
