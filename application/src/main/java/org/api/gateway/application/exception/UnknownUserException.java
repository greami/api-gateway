package org.api.gateway.application.exception;

public class UnknownUserException extends RuntimeException {
    public UnknownUserException(String username, Throwable cause) {
        super("User not found: " + username, cause);
    }
}
