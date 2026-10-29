# ADR-001 - Monorepo

## Status

Accepted

## Context

The project consists of multiple microservices that are developed by a single developer as part of a learning and portfolio project.

## Decision

Use a single Git repository (Monorepo) containing all microservices and shared documentation.

## Consequences

### Advantages

- Easier project navigation
- Single documentation source
- Simpler dependency management
- Easier local development
- Better portfolio presentation

### Disadvantages

- Repository grows over time
- CI/CD may become more complex

## Alternatives Considered

### Multi-repository

Rejected for the initial version because it adds unnecessary complexity for a solo developer.