package io.github.najidnizar.bankclientsmanagement.customer_account_service.client;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import static org.assertj.core.api.Assertions.assertThat;

class BearerTokenRelayInterceptorTest {

    private final BearerTokenRelayInterceptor interceptor = new BearerTokenRelayInterceptor();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAddBearerTokenOfCurrentUser() {
        Jwt jwt = Jwt.withTokenValue("user-access-token")
                .header("alg", "none")
                .claim("preferred_username", "advisor")
                .build();
        SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(jwt));
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertThat(template.headers().get(HttpHeaders.AUTHORIZATION))
                .containsExactly("Bearer user-access-token");
    }

    @Test
    void shouldNotAddHeaderWhenNoUserIsAuthenticated() {
        RequestTemplate template = new RequestTemplate();

        interceptor.apply(template);

        assertThat(template.headers()).doesNotContainKey(HttpHeaders.AUTHORIZATION);
    }
}
