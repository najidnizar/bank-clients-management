package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Plain unit test (no Spring context): controller tests inject authorities directly with jwt().authorities(...),
 * so they bypass this converter. This test checks the Keycloak claim mapping itself.
 */
class KeycloakRoleConverterTest {

    private final KeycloakRoleConverter converter = new KeycloakRoleConverter();

    @Test
    void shouldMapRealmRolesToPrefixedAuthorities() {
        Jwt jwt = jwtWithClaim("realm_access", Map.of("roles", List.of("ADVISOR", "ADMIN")));

        Collection<GrantedAuthority> authorities = converter.convert(jwt);

        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactlyInAnyOrder("ROLE_ADVISOR", "ROLE_ADMIN");
    }

    @Test
    void shouldReturnNoAuthorityWhenRealmAccessClaimIsMissing() {
        Jwt jwt = jwtWithClaim("preferred_username", "advisor");

        assertThat(converter.convert(jwt)).isEmpty();
    }

    @Test
    void shouldReturnNoAuthorityWhenRolesAreMissing() {
        Jwt jwt = jwtWithClaim("realm_access", Map.of("other", "value"));

        assertThat(converter.convert(jwt)).isEmpty();
    }

    private static Jwt jwtWithClaim(String name, Object value) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(name, value)
                .build();
    }
}
