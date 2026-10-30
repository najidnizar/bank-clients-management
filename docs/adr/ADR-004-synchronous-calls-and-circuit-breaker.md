# ADR-004 - Synchronous Calls Protected by a Circuit Breaker

## Status

Accepted

## Context

Before opening a bank account, the account service must make sure the customer exists. This check needs an **immediate answer**: the account must not be created for an unknown customer. Events (ADR-003) cannot provide that guarantee at request time.

## Decision

The account service calls the onboarding service **synchronously**:

- **OpenFeign** client, resolved through **Eureka** by service name (`customer-onboarding-service`) — no hard-coded URL.
- Wrapped in a **Resilience4j circuit breaker** (`customer-service`): count-based window of 10 calls, opens at 50 % failures (after at least 5 calls), stays open 10 s, then lets 3 trial calls through.
- Error mapping:
  - `404` from onboarding → `CustomerNotFoundException` (the request is invalid).
  - Timeout, error or open circuit → fallback raising `CustomerServiceUnavailableException` (the request cannot be processed now).
- The call is visible in distributed tracing: one trace covers both services.

## Consequences

### Advantages

- Strong guarantee at creation time, with a simple programming model.
- When onboarding is down, the account service **fails fast** instead of piling up blocked threads, and recovers automatically.
- A business error (unknown customer) is clearly distinguished from a technical one (service unavailable).

### Disadvantages

- Runtime coupling: account creation is unavailable while onboarding is down.
- Adds latency to account creation.

## Alternatives Considered

### Local replica of customers built from events

The account service could keep its own table of known customers from `customer-events` and validate locally. Rejected for now: it adds storage and eventual-consistency edge cases (a customer created a few milliseconds ago would be rejected). It is the natural evolution if onboarding availability becomes a problem.
