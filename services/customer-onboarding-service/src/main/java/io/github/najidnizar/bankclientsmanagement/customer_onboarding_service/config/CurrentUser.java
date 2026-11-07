package io.github.najidnizar.bankclientsmanagement.customer_onboarding_service.config;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Name of the user performing the current operation, for audit columns (createdBy, updatedBy, changedBy).
 * <p>
 * During an HTTP request, it is the authenticated user ({@code preferred_username} from the JWT, e.g. "advisor").
 * Outside a user request — Kafka consumers, scheduled jobs — there is no authenticated user and the
 * operation is attributed to {@value #SYSTEM}.
 */
public final class CurrentUser {

    public static final String SYSTEM = "SYSTEM";

    private CurrentUser() {
    }

    public static String username() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return SYSTEM;
        }
        return authentication.getName();
    }
}
