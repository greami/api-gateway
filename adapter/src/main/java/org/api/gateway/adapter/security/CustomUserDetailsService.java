package org.api.gateway.adapter.security;

import org.api.gateway.domain.model.userService.UtenteRecord;
import org.api.gateway.domain.port.in.userService.UtenteUseCasePortIn;
import org.api.gateway.domain.port.in.userService.UtenteRuoloUseCasePortIn;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    private static final int ADMIN_ROLE_ID = 2;

    private final UtenteUseCasePortIn utenteUseCasePortIn;
    private final UtenteRuoloUseCasePortIn utenteRuoloUseCasePortIn;

    public CustomUserDetailsService(UtenteUseCasePortIn utenteUseCasePortIn,
                                    UtenteRuoloUseCasePortIn utenteRuoloUseCasePortIn) {
        this.utenteUseCasePortIn = utenteUseCasePortIn;
        this.utenteRuoloUseCasePortIn = utenteRuoloUseCasePortIn;
    }

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
   {
       UtenteRecord utenteRecord = utenteUseCasePortIn.findByNomeUtente(username)
               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

       return org.springframework.security.core.userdetails.User.builder()
               .username(utenteRecord.nomeUtente())
               .password(utenteRecord.password() != null ? utenteRecord.password() : "")
               .authorities(authoritiesFor(utenteRecord))
               .accountExpired(false)
               .accountLocked(false)
               .credentialsExpired(false)
               .disabled(false)
               .build();
   }

   public UserDetails loadByUserNameAndPassword(String username, String password) throws UsernameNotFoundException
   {
       UtenteRecord utenteRecord = utenteUseCasePortIn.findByNomeUtenteAndPassword(username, password)
               .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));



       return org.springframework.security.core.userdetails.User.builder()
               .username(utenteRecord.nomeUtente())
               .password(password)
               .authorities(authoritiesFor(utenteRecord))
               .accountExpired(false)
               .accountLocked(false)
               .credentialsExpired(false)
               .disabled(false)
               .build();
   }

   private List<GrantedAuthority> authoritiesFor(UtenteRecord utenteRecord) {
       boolean admin = utenteRuoloUseCasePortIn.findRuoliByUtente(utenteRecord.id())
               .contains(ADMIN_ROLE_ID);

       return List.of(new SimpleGrantedAuthority(admin ? "ROLE_ADMIN" : "ROLE_USER"));
   }
}
