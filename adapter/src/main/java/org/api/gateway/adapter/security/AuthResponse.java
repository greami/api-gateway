package org.api.gateway.adapter.security;

import java.util.List;

public record AuthResponse(String token, String tokenType, String username, List<String> roles, Integer idUtente)
{
}
