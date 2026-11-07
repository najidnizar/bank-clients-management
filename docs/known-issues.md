# Known Issues and Technical Debt

Tracked here so nothing is lost; each item is a candidate for a dedicated branch.

## Bugs

- [x] ~~**Invalid customer status transition returns HTTP 500** (e.g. blocking a closed customer). `InvalidCustomerStatusTransitionException` is not handled by the onboarding `GlobalExceptionHandler`; it should return `409 Conflict` like the KYC and account services.~~ Fixed: handled by `GlobalExceptionHandler`, returns `409 Conflict`.

## Reliability

- [ ] **Customer events are sent before the database commit** (`KafkaTemplate.send` inside `@Transactional`). A failed commit produces an event for a customer that does not exist; an unavailable broker loses the event. Fix: transactional outbox. See [ADR-003](adr/ADR-003-event-driven-communication-with-kafka.md).
- [x] ~~**Client errors count as failures in the circuit breaker**~~ Fixed: 4xx are ignored by the circuit breaker (`ignore-exceptions`) and rethrown by dedicated fallbacks; covered by `CustomerServiceClientServiceCircuitBreakerTest`.
- [ ] **No dead-letter topic**: a message that still fails after retries is only logged.
- [ ] **No health checks in Docker Compose**: `depends_on` waits for containers to start, not to be ready; services rely on `restart: on-failure`.

## Observability

- [ ] **Trace context is not propagated through Kafka**: enable observation on the `KafkaTemplate` (onboarding) and on the listener container factories (KYC, document, account).
- [ ] **Grafana dashboard and data sources are not provisioned**: export the dashboard as JSON and load it at startup.

## Features

- [ ] Document and account services only log customer events.
- [ ] Document review workflow (`VALIDATED`, `REJECTED`, `EXPIRED`) is defined but not implemented.
- [ ] No authentication / authorization (planned: Keycloak, OAuth2 / JWT).

## API design

- [ ] **Paginated responses serialize `PageImpl` as-is** (`pageable`, `sort`, `empty`... leak into the JSON of `GET /api/v1/customers`). This format is not stable across Spring Data versions. Fix: `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` or return `PagedModel`.

## Cleanup

- [ ] `RestClientConfig` bean in the account service is unused (replaced by OpenFeign).
- [ ] `customer-service.url` in the account service configuration is unused (Feign resolves the service through Eureka).
- [ ] `CustomerService.getCustomers(Pageable)` is no longer called by any endpoint (replaced by `searchCustomers`).
- [ ] `spring.kafka.consumer.*` in the KYC `application-local.yml` is ignored, because Kafka beans are declared in Java.
