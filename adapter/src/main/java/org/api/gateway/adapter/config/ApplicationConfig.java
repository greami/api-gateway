package org.api.gateway.adapter.config;

import org.api.gateway.application.port.out.CoreServicePort;
import org.api.gateway.application.port.out.JwtTokenPort;
import org.api.gateway.application.port.out.UserServicePort;
import org.api.gateway.application.service.AuthenticationService;
import org.api.gateway.application.service.CoreGatewayService;
import org.api.gateway.application.service.UserGatewayService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public CoreGatewayService coreGatewayService(CoreServicePort coreServicePort) {
        return new CoreGatewayService(coreServicePort);
    }

    @Bean
    public UserGatewayService userGatewayService(UserServicePort userServicePort) {
        return new UserGatewayService(userServicePort);
    }

    @Bean
    public AuthenticationService authenticationService(
            UserServicePort userServicePort, JwtTokenPort jwtTokenPort) {
        return new AuthenticationService(userServicePort, jwtTokenPort);
    }
}
