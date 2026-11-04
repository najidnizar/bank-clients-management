package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
         http.authorizeHttpRequests(matcher -> matcher.requestMatchers(
                 "/actuator/health/**", "/actuator/prometheus", "/swagger-ui/**", "/v3/api-docs/**")
                 .permitAll().anyRequest().authenticated());
         http.oauth2ResourceServer(resource -> resource  .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
         http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
         http.csrf(csrf -> csrf.disable());
         return http.build();
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        converter.setPrincipalClaimName("preferred_username");
        return converter;
    }


}
