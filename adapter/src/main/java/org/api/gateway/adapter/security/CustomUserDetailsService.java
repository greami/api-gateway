package org.api.gateway.adapter.security;

import org.api.gateway.application.port.in.LoadUserUseCase;
import org.api.gateway.application.model.AuthenticatedUser;
import org.api.gateway.application.exception.UnknownUserException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final LoadUserUseCase loadUserUseCase;

    public CustomUserDetailsService(LoadUserUseCase loadUserUseCase) {
        this.loadUserUseCase = loadUserUseCase;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            AuthenticatedUser user = loadUserUseCase.loadUserByUsername(username);
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.username())
                    .password(user.password() != null ? user.password() : "")
                    .authorities(user.authorities().toArray(String[]::new))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .disabled(false)
                    .build();
        } catch (UnknownUserException exception) {
            throw new UsernameNotFoundException(exception.getMessage(), exception);
        }
    }
}
