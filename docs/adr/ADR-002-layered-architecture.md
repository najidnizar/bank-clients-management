# ADR-002 - Layered Architecture

## Status

Accepted

## Context

The project aims to reproduce enterprise-grade Spring Boot applications while remaining easy to understand and maintain.

## Decision

Use a Layered Architecture:

Controller
→ Service
→ Repository
→ Database

The project will also use Package by Feature.

## Consequences

### Advantages

- Very common in enterprise Spring Boot applications
- Easy to learn
- Easy to maintain
- Large community support

### Disadvantages

- Domain logic can become coupled to the infrastructure in large applications.

## Future Evolution

Some microservices may later be refactored to Hexagonal Architecture or other architectural styles for comparison purposes.