package org.api.gateway.adapter.config;

import org.api.gateway.adapter.security.JwtAuthenticationFilter;
import org.api.gateway.adapter.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter)
    {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/auth/login").permitAll() //to remove
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers(HttpMethod.GET,  "/utenti/{idUtente}").permitAll()
                       // .requestMatchers("/utenti/**", "/ruoli/**", "/utente-ruoli/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
