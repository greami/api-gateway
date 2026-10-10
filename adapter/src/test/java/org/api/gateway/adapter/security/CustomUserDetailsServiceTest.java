package org.api.gateway.adapter.security;

import org.api.gateway.adapter.client.UserServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.RuoloResponse;
import org.openapitools.model.UtenteConRuoliResponse;
import org.openapitools.model.UtenteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserServiceClient userServiceClient;

    @Test
    void loadsUserAndAdminAuthorityFromUserService() {
        when(userServiceClient.getUtenteByNomeUtente("admin"))
                .thenReturn(ResponseEntity.ok(userWithRoles(
                        "admin", List.of(new RuoloResponse("admin", "Admin", 2)))));

        UserDetails user = new CustomUserDetailsService(userServiceClient).loadUserByUsername("admin");

        assertEquals("admin", user.getUsername());
        assertEquals("ROLE_ADMIN", user.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void validatesCredentialsAndLoadsUserDetails() {
        when(userServiceClient.getUtenteByUserNameAndPassword("user01", "secret"))
                .thenReturn(ResponseEntity.ok(new UtenteResponse("user01", "Luca", "Rossi", "user@example.com", 1000)));
        when(userServiceClient.getUtenteByNomeUtente("user01"))
                .thenReturn(ResponseEntity.ok(userWithRoles("user01", List.of())));

        UserDetails user = new CustomUserDetailsService(userServiceClient)
                .loadByUserNameAndPassword("user01", "secret");

        assertEquals("user01", user.getUsername());
        assertEquals("ROLE_USER", user.getAuthorities().iterator().next().getAuthority());
        verify(userServiceClient).getUtenteByUserNameAndPassword("user01", "secret");
    }

    @Test
    void rejectsMissingUsersAndInvalidCredentials() {
        when(userServiceClient.getUtenteByNomeUtente("missing"))
                .thenReturn(ResponseEntity.<UtenteConRuoliResponse>ok().build());
        when(userServiceClient.getUtenteByUserNameAndPassword("user01", "wrong"))
                .thenReturn(ResponseEntity.<UtenteResponse>ok().build());
        CustomUserDetailsService service = new CustomUserDetailsService(userServiceClient);

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing"));
        assertThrows(UsernameNotFoundException.class, () -> service.loadByUserNameAndPassword("user01", "wrong"));
    }

    private UtenteConRuoliResponse userWithRoles(String username, List<RuoloResponse> roles) {
        return new UtenteConRuoliResponse(username, "Luca", "Rossi", "user@example.com", 1000, roles, "secret");
    }
}
