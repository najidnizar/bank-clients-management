# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Learning/portfolio monorepo reproducing banking customer-management workflows with Java 25, Spring Boot 4 (4.1.x), Spring Cloud, PostgreSQL, Kafka, and Grafana-stack observability. Design docs live in `docs/` (ADRs, architecture overview, development standards, customer domain model); `frontend/` and `infrastructure/{kafka,kubernetes,monitoring}` are still empty placeholders.

## Commands

There is no parent POM — each service under `services/` is an independent Maven project with its own `mvnw`. Run commands from inside the service directory.

```bash
# Local infrastructure only (Postgres x4, MinIO, Kafka, Prometheus, Grafana, Tempo) — run services from the IDE
docker compose -f infrastructure/docker/docker-compose.yml up -d

# Everything in Docker (infra + the 6 services, Compose profile "apps"); stop IDE-run services first (same ports)
docker compose -f infrastructure/docker/docker-compose.yml --profile apps up -d --build

# Build / test a service
cd services/customer-onboarding-service
./mvnw clean verify
./mvnw test -Dtest=CustomerServiceTest                 # single class
./mvnw test -Dtest=CustomerServiceTest#methodName      # single method

# Run a service (no profile is active by default; DB/Kafka/Eureka config lives in application-local.yml)
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Each service has a multi-stage `Dockerfile` (JDK 25 build → JRE 25 runtime, non-root). In containers, addresses are overridden by env vars set in the Compose file (`SPRING_DATASOURCE_URL`, `SPRING_KAFKA_BOOTSTRAP_SERVERS`, `EUREKA_SERVER_URL`, `MINIO_URL`, `MANAGEMENT_OPENTELEMETRY_TRACING_EXPORT_OTLP_ENDPOINT`); without them every value falls back to `localhost`. Kafka configs are Java beans, so they read `spring.kafka.bootstrap-servers` via `@Value` — `spring.kafka.*` YAML keys are otherwise ignored.

CI (`.github/workflows/ci.yml`): per-service matrix running `./mvnw -B verify`, then a Docker image build check (no push).

Repository/integration tests extend `config/AbstractPostgresIntegrationTest` (Testcontainers `postgres:17` with `@ServiceConnection`), so Docker must be running for tests. Controller tests use `@WebMvcTest` + `@MockitoBean`; Jackson is v3 (`tools.jackson.databind`).

Manual API requests are in the Bruno collection at `bruno/bank-clients-management/` (environment `Local`: `baseUrl` = onboarding service directly, `baseUrlGateway` = gateway).

## Services and ports

| Service | Port | DB (host port) | Notes |
|---|---|---|---|
| service-discovery | 8761 | — | Eureka server; start first |
| customer-api-gateway | 8088 | — | Spring Cloud Gateway (WebMVC), routes `lb://` by path |
| customer-onboarding-service | 8080 | 5432 `customer_onboarding_db` | Owns the Customer aggregate; Kafka **producer** |
| customer-kyc-service | 8081 | 5433 `customer_kyc_db` | Kafka consumer; auto-creates KYC cases |
| customer-document-service | 8082 | 5434 `customer_document_db` | Kafka consumer; files stored in MinIO (9000/9001) |
| customer-account-service | 8083 | 5435 `customer_account_db` | Kafka consumer; calls onboarding via OpenFeign |

Gateway routes: `/api/v1/customers/**`, `/api/v1/kyc-cases/**`, `/api/v1/documents/**`, `/api/v1/accounts/**`. Observability: Prometheus (9090) scrapes `/actuator/prometheus` on 8080–8083 via `host.docker.internal`; traces go via OTLP to Tempo (`localhost:4318`); Grafana on 3000.

## Architecture

- **Database per service**, schema managed exclusively by Flyway (`src/main/resources/db/migration/V{n}__Description.sql`) with `ddl-auto: validate` — any entity change needs a new migration; never edit an applied one.
- **Event flow:** onboarding publishes `CustomerEvent(eventType, customerId, customerNumber, status)` to the `customer-events` topic (key = customerId) on `CUSTOMER_CREATED` and `CUSTOMER_STATUS_CHANGED`. KYC, document and account services each consume with their own consumer group. There is no shared library: each consumer keeps its **own copy** of `CustomerEvent`/`CustomerEventType` in its `event` package, so changes to the event contract must be mirrored in every service (KYC also pins the JSON default type/trusted packages in `application-local.yml`).
- **Synchronous call:** account-service validates customers through `CustomerServiceFeignClient` (Eureka name `customer-onboarding-service`) wrapped by `CustomerServiceClientService` with a Resilience4j `customer-service` circuit breaker; 404 → `CustomerNotFoundException`, fallback → `CustomerServiceUnavailableException`.
- **Status state machines:** customers, KYC cases and bank accounts each have a status enum with transitions validated in the service layer (`validateStatusTransition` / `Invalid*StatusTransitionException`); customer and account status changes are also recorded in `*_status_history` tables.
- **Customer identifiers:** UUID `id` (technical) plus business `customerNumber` in format `C000000001`, generated from a DB sequence (see `docs/domain/customer-domain.md`).

## Conventions

- Layered architecture (Controller → Service → Repository) per ADR-002. Within each service, packages: `controller`, `service`, `repository`, `entity` (account-service uses `domain`), `dto`, `mapper` (MapStruct), `exception` (`GlobalExceptionHandler` + `ApiErrorResponse`), `event`, `config`, `client`.
- Root package `io.github.najidnizar.bankclientsmanagement.<service_name_with_underscores>` (gateway and discovery use `io.github.najidnizar.<name>`).
- REST APIs are versioned under `/api/v1/...`; DB tables/columns are `snake_case`.
- Lombok + MapStruct annotation processors are configured in each `pom.xml`.
- Spring profiles: `local`, `test`, `dev`, `prod`. The active profile is never committed; it is set by the run environment.
- Git: feature branches (`feature/...`, `bugfix/...`) off `main`; Conventional Commits (`feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`, `build:`).
