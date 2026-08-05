# Design: Backend Foundation

## Technical Approach

Implement a Spring Boot 4.1.0 / Java 21 REST API under `/api/v1` with two bounded contexts, `event` and `pos`, and a thin `shared` kernel. Domain and application code are framework-free; infrastructure adapts ports to JPA; interfaces translate HTTP to use cases. Event owns `EventPointOfSaleLink`; POS remains standalone. The 11 endpoints serve 12 use cases because B01 and B02 share event search.

## Package Tree and Dependency Direction

```text
com.entreck
├── event/{domain,application,infrastructure,interfaces}
├── pos/{domain,application,infrastructure,interfaces}
├── shared/domain/{id,enums,link,valueobject}
├── shared/web
└── config
```

`interfaces -> application -> domain`; `infrastructure -> domain`; domain has no Spring, JPA, or web imports. Repository ports live in domain and adapters in infrastructure.

## Data Model

- `Event`: `EventId`, name, `EventCategory`, date, description, `OrganizerId`, `EventStatus`, timestamps. Lifecycle is `DRAFT -> PUBLISHED -> CANCELLED` through domain methods.
- `PointOfSale`: `PointOfSaleId`, name, `Address`, `GeoLocation`, `Contact`, `OpeningHours`, `ShopId`, status, timestamps.
- `EventPointOfSaleLink`: `LinkId`, `EventId`, `PointOfSaleId`, availability, note, `updatedAt`; `(event_id, pos_id)` is unique and the aggregate belongs to event.
- IDs are immutable typed `record`s wrapping non-null `Long`: `EventId`, `PointOfSaleId`, `OrganizerId`, `ShopId`, and `LinkId`.
- Value objects are immutable validated records: address requires meaningful fields; latitude is `[-90,90]`; longitude `[-180,180]`; country is ISO-3166; contact validates email/phone; each weekly window requires `open < close`.
- Enums include event categories, event/POS status, and `AvailabilityStatus`.

## API Contract

| Method | Path | Use cases |
|---|---|---|
| GET | `/api/v1/events` | B01, B02 |
| GET | `/api/v1/events/{id}` | B03 |
| GET | `/api/v1/events/{id}/points-of-sale` | B04 |
| GET | `/api/v1/points-of-sale/{id}` | B06 |
| POST/PATCH | `/api/v1/events[/{id}]` | O01/O02 |
| POST/DELETE | `/api/v1/events/{id}/points-of-sale[/{posId}]` | O03/O04 |
| POST/PATCH | `/api/v1/points-of-sale[/{id}]` | S01/S02 |
| PUT | `/api/v1/points-of-sale/{posId}/events/{eventId}/availability` | S03 |

Create/update requests are validated. PATCH is for partial O02/S02 updates; PUT is reserved for the single availability replacement. Spring `Page<T>` is mapped to `PageResponse<T>` with `PageMeta`; requested size is capped at 100.

Errors use `ErrorResponse(code, message, correlationId, fieldErrors)` and `FieldErrorItem(field, message)`: 400 `VALIDATION_ERROR`, 404 event/POS/link not found, 422 duplicate name/link, and 500 `INTERNAL_ERROR` with a correlation ID.

## Persistence and Flow

`Controller -> UseCase -> Repository port -> JPA adapter -> Spring Data -> PostgreSQL`; static mappers convert between domain objects and `@Entity` classes. `EventJpaEntity`, `PointOfSaleJpaEntity`, and `EventPointOfSaleLinkJpaEntity` keep JPA annotations out of domain. The link table has a database uniqueness constraint as a second line of defense.

## Locked ADRs

1. Two bounded contexts plus shared kernel: limits coupling while sharing primitives.
2. Event owns the link and POS is standalone: association behavior follows event lifecycle.
3. POJO/record domain, JPA adapters, static mappers: preserves framework independence.
4. Immutable validated value-object records: centralizes invariants.
5. Typed `Long` ID records: prevents accidental ID mixing.
6. `/api/v1` versioning: permits future contract evolution.
7. `Page<T>` to `PageResponse<T>`, max 100: hides Spring types and bounds load.
8. Permit-all `SecurityFilterChain`: provides a JWT filter seam without v1 auth.
9. Testcontainers over H2: tests real PostgreSQL behavior.
10. Boot 4.1/Security 7/JUnit 6 and split test starters: matches the pinned stack.
11. Plain PostgreSQL 16, no PostGIS: nearby search is deferred.
12. `ddl-auto: update`, Flyway later: reduces v1 setup while requiring a pre-production migration step.
13. PATCH for partial updates, PUT only for availability: matches mutation semantics.

## Testing and Runtime

`*Test` covers domain/use cases without Spring; `*IT` uses `@DataJpaTest` and a reusable Testcontainers `postgres:16`; `*WebMvcTest` covers validation, status codes, JSON, and error mapping; `BackendApplicationSmokeTest` covers full wiring and health. Docker uses a multi-stage Temurin 21 image, non-root runtime, PostgreSQL 16 compose health checks, and a named volume. No H2 or PostGIS is used.

## Delivery and Risks

Six chained slices are bootstrap, event domain/application, event infrastructure/API, POS domain/application, POS infrastructure/API, and link/cross-cutting; each targets <=400 lines. Until JWT exists, organizer/shop IDs are trusted from writes and deployment MUST be behind a private network or auth proxy. Flyway MUST be added before production.

## Reconciliation Item

One earlier locked scope record simplified `AvailabilityStatus` to available/unavailable/unknown, while this design and `tasks.md` use `AVAILABLE/LIMITED/SOLD_OUT/UNKNOWN`. Reconcile the enum and API contract before implementation; this artifact intentionally does not silently choose between them.
