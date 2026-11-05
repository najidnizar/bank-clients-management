package io.github.najidnizar.bankclientsmanagement.customer_account_service.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Relays the caller's access token on every outgoing Feign request.
 * <p>
 * Calls made while handling a user request (e.g. opening an account) are made <b>on behalf of that user</b>:
 * the downstream service sees the same token, so it applies its own security rules to the real user.
 * <p>
 * The token is read from the {@link SecurityContextHolder}, which is bound to the current thread:
 * the Feign call must run on the request thread (it does: the circuit breaker is synchronous).
 * Calls without a user (scheduled jobs, Kafka consumers) would need the client credentials flow instead.
 */
@Component
public class BearerTokenRelayInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            template.header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtAuthentication.getToken().getTokenValue());
        }
        // No JWT in context: nothing is added and the downstream service answers 401, which is the expected behavior.
    }
}
