package org.api.gateway.adapter.security;

import org.api.gateway.adapter.client.UserServiceClient;
import org.openapitools.model.RuoloResponse;
import org.openapitools.model.UtenteConRuoliResponse;
import org.openapitools.model.UtenteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final int ADMIN_ROLE_ID = 2;

    private final UserServiceClient userServiceClient;

    public CustomUserDetailsService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ResponseEntity<UtenteConRuoliResponse> response = userServiceClient.getUtenteByNomeUtente(username);
        UtenteConRuoliResponse user = response.getBody();
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return buildUserDetails(user);
    }

    public UserDetails loadByUserNameAndPassword(String username, String password)
            throws UsernameNotFoundException {
        ResponseEntity<UtenteResponse> response =
                userServiceClient.getUtenteByUserNameAndPassword(username, password);
        if (response.getBody() == null) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        return loadUserByUsername(username);
    }

    private UserDetails buildUserDetails(UtenteConRuoliResponse user) {
        boolean admin = user.getRuoli().stream()
                .map(RuoloResponse::getIdRuolo)
                .anyMatch(roleId -> Integer.valueOf(ADMIN_ROLE_ID).equals(roleId));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getNomeUtente())
                .password(user.getPassword() != null ? user.getPassword() : "")
                .authorities(new SimpleGrantedAuthority(admin ? "ROLE_ADMIN" : "ROLE_USER"))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
