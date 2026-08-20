package org.api.gateway.adapter.security;

import org.api.gateway.domain.model.userService.UtenteRecord;
import org.api.gateway.domain.port.in.userService.UtenteUseCasePortIn;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
   private final UtenteUseCasePortIn utenteUseCasePortIn;

   public CustomUserDetailsService(UtenteUseCasePortIn utenteUseCasePortIn) {
       this.utenteUseCasePortIn = utenteUseCasePortIn;
   }

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
   {
       UtenteRecord utenteRecord = utenteUseCasePortIn.findByNomeUtente(username)
               .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

       return org.springframework.security.core.userdetails.User.builder()
               .username(utenteRecord.nomeUtente())
               .password(utenteRecord.password() != null ? utenteRecord.password() : "")
               .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
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
               .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
               .accountExpired(false)
               .accountLocked(false)
               .credentialsExpired(false)
               .disabled(false)
               .build();
   }
}
