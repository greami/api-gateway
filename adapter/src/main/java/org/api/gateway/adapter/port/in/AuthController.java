package org.api.gateway.adapter.port.in;

import org.api.gateway.adapter.security.AuthRequest;
import org.api.gateway.adapter.security.AuthResponse;
import org.api.gateway.adapter.security.CustomUserDetailsService;
import org.api.gateway.adapter.security.JwtService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController
{
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;

    public AuthController(CustomUserDetailsService customUserDetailsService, JwtService jwtService)
    {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest)
    {
        UserDetails userDetails = customUserDetailsService.loadByUserNameAndPassword(
                authRequest.username(),
                authRequest.password()
        );

        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                )
        );
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                "Bearer",
                authRequest.username()
        );
    }
}
