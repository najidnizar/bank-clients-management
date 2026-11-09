# ADR-006 - Eureka and Spring Cloud Gateway on Kubernetes

## Status

Accepted

## Context

The platform was built for Docker Compose with Spring Cloud: **Eureka** for service discovery and client-side load balancing, and **Spring Cloud Gateway** as the single entry point (path routing, JWT validation).

Kubernetes provides equivalent building blocks natively:

- **Service discovery and load balancing**: every Service gets a stable DNS name (`customer-onboarding-service`) that balances across its pods.
- **HTTP entry point**: the Gateway API (implemented here by Traefik) routes external traffic by host name or path.

On Kubernetes, Eureka is therefore redundant, and Spring Cloud Gateway overlaps with the Gateway API.

## Decision

Keep Eureka and Spring Cloud Gateway in the Kubernetes deployment, unchanged:

- The same images and configuration run on Docker Compose and on Kubernetes.
- The Gateway API is the **cluster entry point** (host-based: `api.localhost`, `keycloak.localhost`); Spring Cloud Gateway remains the **application entry point** (path-based routing to the services, JWT validation at the edge).

## Consequences

### Advantages

- One code base and one configuration for both runtimes; no Kubernetes-specific code path.
- Shows both approaches, the Spring Cloud one being still common outside Kubernetes.

### Disadvantages

- Two discovery mechanisms (Eureka registry and Kubernetes DNS) and two gateways: more components to run and understand.
- Eureka keeps its own view of the instances; Kubernetes already knows which pods are ready (readiness probes). The two can briefly disagree during a rollout.
- Extra pods and memory (Eureka, Spring Cloud Gateway).

## What a production Kubernetes deployment would do

- **Remove Eureka**: call services by their Kubernetes Service name (`http://customer-onboarding-service:8080`), or use Spring Cloud Kubernetes discovery if client-side load balancing is still wanted. Load balancing and health-based routing then come from Kubernetes and the readiness probes.
- **Spring Cloud Gateway**: either keep it as an application gateway (aggregation, rate limiting, token checks close to the code), or move routing to the Gateway API (`HTTPRoute` per path) and rely on each service's own JWT validation, which this project already has (defense in depth, ADR-005).

## Alternatives Considered

### Remove Eureka now

Rejected for now: it would touch the Feign client, the gateway routes and the configuration of every service, with no visible change in behaviour. The trade-off is documented here instead; a Kubernetes-only profile could switch to Service names later.

### Replace Spring Cloud Gateway by Gateway API routes

Rejected for now: same reasoning, and Spring Cloud Gateway is still the entry point in the Docker Compose setup.
