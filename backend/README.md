# Fake Data Generator — Backend (Phase 1)

Spring Boot service that generates fake records from user-defined models.

## What's included

- `model/` — `ModelDefinition`, `FieldDefinition`, `FieldType` enum
- `generator/`
  - `FieldGenerator` strategy interface
  - `FieldGeneratorRegistry` — auto-wires every `FieldGenerator` Spring bean
  - `ModelGeneratorEngine` — produces records given a model + context
  - `GenerationContext` — shared Faker, sequence counters, reference pools
  - `ReferencePool` — bounded ring buffer feeding REFERENCE fields
  - `fieldtypes/` — 25+ field generator implementations
- `Phase1Demo` — `CommandLineRunner` that prints sample records

## Supported field types

| Category    | Types |
|-------------|-------|
| Identifiers | UUID, SEQUENCE, REFERENCE |
| Primitives  | INTEGER, DECIMAL, BOOLEAN, STRING |
| Personal    | FIRST_NAME, LAST_NAME, FULL_NAME, EMAIL, PHONE, DATE_OF_BIRTH |
| Address     | STREET, CITY, STATE, COUNTRY, POSTAL_CODE |
| Finance     | IBAN, ACCOUNT_NUMBER, CURRENCY, CUSIP, ISIN, TICKER |
| Date/time   | DATE, TIMESTAMP |
| Choice      | ENUM, WEIGHTED_ENUM |
| Pattern     | REGEX |

## Run

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=phase1
```

## Test

```bash
mvn test
```

