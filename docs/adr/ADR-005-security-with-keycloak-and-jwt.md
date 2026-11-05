# ADR-005 - Security with Keycloak, OAuth 2.0 and JWT

## Status

Accepted

## Context

The platform handles customer data and banking operations: every API call must be authenticated, and operations must be restricted by business role (advisor, compliance officer, administrator). Services are reachable individually (a misconfigured network, an attacker inside it), so security cannot rely on the gateway alone.

## Decision

- **Identity provider**: Keycloak, using the standard protocols **OAuth 2.0 / OpenID Connect**. The `bank` realm (roles, clients, demo users) is versioned in Git and imported at startup.
- **Access tokens**: signed **JWTs**, short-lived (15 minutes), with a dedicated audience `bank-api`.
- **Resource servers**: the API gateway **and every microservice** validate the token locally (signature with Keycloak's public keys, expiry, issuer, audience) — *defense in depth*.
- **Authorization**: Keycloak realm roles (`realm_access.roles`) are mapped to Spring authorities `ROLE_*`; business rules are declared on controller methods with `@PreAuthorize`.
- **Service-to-service calls** made on behalf of a user **relay the user's token** (Feign `RequestInterceptor`).
- Stateless APIs: no HTTP session, CSRF protection disabled (no cookie-based authentication).

Details: [docs/security.md](../security.md).

## Consequences

### Advantages

- Standard protocols: the Spring code does not depend on Keycloak; Microsoft Entra ID, Okta or Auth0 could replace it by changing the issuer URL and the role mapping.
- No call to Keycloak per request: validation is local and scales with the services.
- A service reached directly, bypassing the gateway, still rejects unauthenticated or unauthorized calls.
- The downstream service in a synchronous call knows the real user and applies its own rules.
- Security rules are covered by tests (401 without token, 403 with the wrong role).

### Disadvantages

- A JWT cannot be revoked before it expires (mitigated by the short lifetime).
- Token validation and the role converter are duplicated in each service (no shared library, see ADR-003).
- Token relay depends on the security context being bound to the request thread; calls without a user (Kafka consumers, scheduled jobs) will need the client credentials grant.
- The local setup is not production-ready: Keycloak in development mode without a database, HTTP, trivial demo passwords, password grant enabled for developer tools.

## Alternatives Considered

### Validation at the gateway only

Rejected: services would trust any request reaching them, so a single network mistake would expose every endpoint.

### Token introspection (asking Keycloak for each request)

Rejected: one extra network call per request and a hard runtime dependency on Keycloak. It would allow immediate revocation, which short-lived tokens make less necessary here.

### Spring Authorization Server or a managed provider (Entra ID, Okta)

Spring Authorization Server would mean building and maintaining an identity provider; a managed provider cannot run locally and its configuration cannot be versioned with the project. Keycloak is widely used, runs in Docker and keeps the integration standard.
