# ADR-002 - Layered Architecture, Package by Layer

## Status

Accepted

## Context

Each microservice owns a single aggregate (customer, KYC case, account, document) and a small number of use cases. The code must be easy to read for any Spring developer.

## Decision

Use a **layered architecture** inside each service:

Controller → Service → Repository → Database

Packages are organized **by layer** at the root of each service: `controller`, `service`, `repository`, `entity` (`domain` in the account service), `dto`, `mapper`, `exception`, `event`, `config`, `client`.

- Business rules and state transitions live in the **service** layer.
- Controllers only map HTTP to service calls; entities are never exposed — DTOs are mapped with MapStruct.
- Errors are translated to HTTP responses in one place (`GlobalExceptionHandler`).

## Consequences

### Advantages

- The most common structure in enterprise Spring Boot code: no learning curve.
- With one aggregate per service, the service boundary already does the job that "package by feature" would do in a monolith.

### Disadvantages

- Domain logic depends on JPA entities, so it is coupled to the persistence technology.
- Would not scale well if a service grew several aggregates; it would then move to package by feature.

## Alternatives Considered

### Package by feature

Rejected: with a single feature per service it only adds one level of nesting.

### Hexagonal architecture

Rejected for now: ports and adapters add indirection that is not justified by the current, mostly CRUD-plus-state-machine logic. It remains an option for a service with richer rules (for example KYC risk scoring).
