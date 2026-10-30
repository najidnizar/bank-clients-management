# ADR-003 - Event-Driven Communication with Kafka

## Status

Accepted

## Context

When a customer is created or changes status, several services must react: the KYC service opens or expires a case, and the document and account services need to know about the customer. The onboarding service should not know who these consumers are, and it must keep working if one of them is down.

## Decision

The onboarding service publishes **domain events** to a Kafka topic, `customer-events`:

- Event types: `CUSTOMER_CREATED`, `CUSTOMER_STATUS_CHANGED`.
- Payload: `eventType`, `customerId`, `customerNumber`, `status` — identifiers and state only, no personal data.
- Message key: the **customer id**, so all events of one customer go to the same partition and are consumed **in order**.
- Each consuming service uses **its own consumer group** and therefore receives every event.
- Consumers are **idempotent** (for example, the KYC service never creates a second case for the same customer), because Kafka guarantees at-least-once delivery.
- Failed messages are retried with a fixed back-off before being logged and skipped.

There is **no shared library** for the event contract: each consumer keeps its own copy of `CustomerEvent`.

## Consequences

### Advantages

- Loose coupling: new consumers can be added without touching the onboarding service.
- Availability: onboarding does not depend on the consumers being up; they catch up from the topic.
- Services stay independently buildable and deployable (no shared artifact to version).

### Disadvantages

- Eventual consistency: the KYC case appears shortly after the customer, not in the same transaction.
- The event is sent from inside the database transaction, before the commit, and without an outbox: if the commit fails, consumers receive an event for a customer that was never saved; if Kafka is unavailable, the event is lost. A **transactional outbox** would make the database write and the event atomic.
- Duplicated event classes must be kept in sync manually when the contract changes.
- No dead-letter topic yet: a message that keeps failing is only logged.

## Alternatives Considered

### Synchronous REST calls from onboarding to each service

Rejected: creates temporal coupling and makes onboarding fail when any downstream service is down.

### Shared event library

Rejected: couples the release cycle of every service to one artifact. A schema registry (Avro / JSON Schema) would be the next step if the contract grows.
