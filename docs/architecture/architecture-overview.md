# Bank Clients Management - Architecture Overview

## Objective

Build a modern enterprise banking platform to learn and demonstrate:

* Java 25 LTS
* Spring Boot 4
* Microservices
* REST APIs
* PostgreSQL
* Kafka
* Docker
* Kubernetes
* Monitoring & Distributed Tracing
* React

The goal is to reproduce realistic banking workflows using modern software engineering practices without reproducing proprietary implementations.

---

# High-Level Architecture

## Microservices

* Customer Onboarding Service
* Customer Profile Service
* Customer Document Service
* Customer KYC Service

## Communication

### Phase 1

* REST APIs

### Phase 2

* Kafka Events

## Databases

Each microservice owns its own PostgreSQL database.

---

# Repository Structure

```text
bank-clients-management
│
├── docs
│   ├── architecture
│   └── diagrams
│
├── services
│
├── infrastructure
│   ├── docker
│   ├── kubernetes
│   ├── monitoring
│   └── kafka
│
├── frontend
│
└── README.md
```

---

# Development Standards

## Repository

* Monorepo architecture

## Java Package Convention

Root package:

`io.github.najidnizar.bankclientsmanagement`

## REST API Convention

* Versioned APIs
* `/api/v1/...`

## Database Convention

* One PostgreSQL database per microservice
* Tables use `snake_case`

## Naming Convention

### Java

* Classes → PascalCase
* Methods → camelCase
* Variables → camelCase

### Maven

* Modules → kebab-case

### Microservices

Examples:

* customer-onboarding-service
* customer-profile-service

---

# Current Decisions

* Monorepo
* One PostgreSQL database per microservice
* REST communication first
* Kafka introduced in a second phase
* Architecture documented with Draw.io diagrams

---

# Future Work

* Internal architecture of each microservice
* Domain model
* REST API design
* Event-driven architecture
* Docker
* Kubernetes
* Monitoring
* Distributed tracing
