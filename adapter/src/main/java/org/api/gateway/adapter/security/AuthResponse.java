package org.api.gateway.adapter.security;

public record AuthResponse(String token, String tokenType, String username)
{
}
