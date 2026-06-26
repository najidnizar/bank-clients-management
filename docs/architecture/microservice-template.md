# Microservice Template

## Technology Stack

- Java 25 LTS
- Spring Boot 4
- Maven
- PostgreSQL

## Dependencies

| Dependency           | Purpose                  |
|----------------------|--------------------------|
| Spring Web           | REST APIs                |
| Spring Data JPA      | Database access          |
| PostgreSQL Driver    | PostgreSQL connectivity  |
| Validation           | Request validation       |
| Lombok               | Reduce boilerplate code  |
| Flyway               | Database migrations      |
| Springdoc OpenAPI    | API documentation        |
| Spring Boot Actuator | Monitoring endpoints     |
| MapStruct            | Entity / DTO mapping     |
| Spring Boot DevTools | Development productivity |

## Architecture

- Layered Architecture
- Package by Feature
- One database per microservice
- REST communication