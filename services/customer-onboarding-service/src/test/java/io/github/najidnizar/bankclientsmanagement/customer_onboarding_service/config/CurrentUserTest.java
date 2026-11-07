package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CurrentUserTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnAuthenticatedUsername() {
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "none").claim("preferred_username", "advisor").build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt, List.of(), "advisor"));

        assertThat(CurrentUser.username()).isEqualTo("advisor");
    }

    @Test
    void shouldReturnSystemWhenNoUserIsAuthenticated() {
        // e.g. a Kafka consumer or a scheduled job
        assertThat(CurrentUser.username()).isEqualTo(CurrentUser.SYSTEM);
    }

    @Test
    void shouldReturnSystemForAnonymousUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));

        assertThat(CurrentUser.username()).isEqualTo(CurrentUser.SYSTEM);
    }
}
