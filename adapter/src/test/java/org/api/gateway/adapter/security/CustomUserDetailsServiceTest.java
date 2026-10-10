package org.api.gateway.adapter.security;

import org.api.gateway.application.port.in.LoadUserUseCase;
import org.api.gateway.application.model.AuthenticatedUser;
import org.api.gateway.application.exception.UnknownUserException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private LoadUserUseCase loadUserUseCase;

    @Test
    void loadsUserAndAdminAuthorityFromUserService() {
        when(loadUserUseCase.loadUserByUsername("admin"))
                .thenReturn(new AuthenticatedUser("admin", "secret", List.of("ROLE_ADMIN")));

        UserDetails user = new CustomUserDetailsService(loadUserUseCase).loadUserByUsername("admin");

        assertEquals("admin", user.getUsername());
        assertEquals("ROLE_ADMIN", user.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadsUserAndStandardAuthorityFromApplicationUseCase() {
        when(loadUserUseCase.loadUserByUsername("user01"))
                .thenReturn(new AuthenticatedUser("user01", "secret", List.of("ROLE_USER")));

        UserDetails user = new CustomUserDetailsService(loadUserUseCase).loadUserByUsername("user01");

        assertEquals("user01", user.getUsername());
        assertEquals("ROLE_USER", user.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void translatesMissingDomainUserToSpringSecurityException() {
        when(loadUserUseCase.loadUserByUsername("missing"))
                .thenThrow(new UnknownUserException("missing", new RuntimeException("missing")));
        CustomUserDetailsService service = new CustomUserDetailsService(loadUserUseCase);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing"));
    }
}
