# Customer Domain

## Purpose

Represents a bank customer managed by the Bank Clients Management platform.

## Responsibilities

- Store customer identity information
- Store contact information
- Support onboarding process
- Be uniquely identifiable
- Be auditable

## Ubiquitous Language

- Customer: A person who has or will have a relationship with the bank.


## Customer Lifecycle

1. Customer onboarding starts.
2. Customer information is collected.
3. Customer is created in the system.

## Business Rules

(To be completed)

## Attributes

| Attribute      | Type      | Category  | Required | Description                                  |
|----------------|-----------|-----------|----------|----------------------------------------------|
| id             | UUID      | Technical | Yes      | Internal immutable identifier.               |
| customerNumber | String    | Business  | Yes      | Unique customer identifier visible to users. |
| firstName      | String    | Business  | Yes      | Customer first name.                         |
| lastName       | String    | Business  | Yes      | Customer last name.                          |
| dateOfBirth    | LocalDate | Business  | Yes      | Customer date of birth.                      |
| email          | String    | Business  | Yes      | Customer email address.                      |
| phoneNumber    | String    | Business  | Yes      | Customer phone number.                       |
| createdAt      | Instant   | Technical | Yes      | Date and time when the customer was created. |
| updatedAt      | Instant   | Technical | Yes      | Date and time of the last customer update.   |

## Design Decisions

### DD-001 - Customer Identification

A customer has two identifiers:

- `id` (UUID): Technical identifier used internally by the system.
- `customerNumber`: Business identifier visible to bank employees and customers.

### DD-002 - Customer Number Format

The customer number is a business identifier.

Format:

C000000001

Rationale:

- Human-readable
- Easy to communicate by phone
- Easy to search
- Independent from the technical UUID

## Database Mapping

| Attribute       | SQL Type                 | Nullable |
|-----------------|--------------------------|----------|
| id              | UUID                     | No       |
| customer_number | VARCHAR(10)              | No       |
| first_name      | VARCHAR(100)             | No       |
| last_name       | VARCHAR(100)             | No       |
| date_of_birth   | DATE                     | No       |
| email           | VARCHAR(255)             | No       |
| phone_number    | VARCHAR(30)              | No       |
| created_at      | TIMESTAMP WITH TIME ZONE | No       |
| updated_at      | TIMESTAMP WITH TIME ZONE | No       |