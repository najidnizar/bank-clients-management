# Development Standards

## Project Structure

## Package Structure

We organize code by feature rather than by technical layer.

Each feature contains its own packages.

Example:

customer/
├── controller
├── service
├── repository
├── entity
├── dto

Additional packages are created only when required:

- mapper
- exception
- validator
- event
- client
- config

## Branching Strategy

### Main Branch

The `main` branch always contains a stable version of the project.

### Feature Branches

Every new feature is developed in its own branch.

Examples:

- feature/customer-onboarding
- feature/customer-profile
- feature/docker
- feature/kubernetes

### Bug Fix Branches

Bug fixes are developed in dedicated branches.

Examples:

- bugfix/customer-validation

## Commit Convention

We use Conventional Commits.

Examples:

- feat: add customer creation endpoint
- fix: validate customer email
- refactor: simplify customer service
- test: add integration tests
- docs: update architecture documentation
- chore: configure Docker
- build: upgrade dependencies

## Code Style

## REST API Standards

## Exception Handling

## Validation

## Logging

## Testing Strategy

## Database Standards

## Docker Standards

## Kubernetes Standards

## Documentation Standards