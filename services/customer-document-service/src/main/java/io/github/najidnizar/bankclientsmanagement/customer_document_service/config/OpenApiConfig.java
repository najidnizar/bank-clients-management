package io.github.najidnizar.bankclientsmanagement.customer_document_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Declares the bearer JWT security scheme in the OpenAPI document, so that Swagger UI shows an
 * "Authorize" button and sends the token with every "Try it out" request.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Customer Document API", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Keycloak access token. Get one with: ./scripts/get-token.sh advisor")
public class OpenApiConfig {
}
