# Proposal: Backend Foundation

## Intent

Build the Entreck v1 backend that lets buyers discover events and points of sale, while organizers and shops manage their own event and POS data. The API must establish a stable Clean Architecture foundation without pulling deferred authentication, nearby search, or ticket commerce into the MVP.

## Scope

### In Scope
- Spring Boot 4.1.0, Java 21, PostgreSQL 16, and `/api/v1` REST API.
- Event and POS bounded contexts plus a shared kernel, with 11 endpoints serving 12 use cases: B01/B02, B03, B04, B06, O01-O04, and S01-S03.
- Event-POS association and per-link availability, Docker/Testcontainers parity, and balanced unit, adapter, controller, and smoke tests.
- Six chained delivery slices using `feature-branch-chain`, each targeting at most 400 changed lines.

### Out of Scope
- Nearby/PostGIS search (B05), JWT/authentication, Flyway, ticket sales/payments, real-time inventory, social login, admin dashboard, and the mobile app.

## Capabilities

### New Capabilities
- `event`: Event lifecycle, search, filtering, pagination, and detail API.
- `pos`: PointOfSale registration, update, validation, and detail API.
- `event-pos-link`: Event-owned association, dissociation, linked POS listing, and availability.
- `shared`: Typed IDs, enums, pagination, error envelopes, and exception mapping.

### Modified Capabilities
- None; these capability specs are restored as new files in this change checkout.

## Approach

Keep domain objects and validated records framework-free. Application use cases depend on repository ports; JPA entities, Spring Data adapters, static mappers, and REST controllers remain outside the domain. Use `SecurityFilterChain` with permit-all as the v1 JWT seam. Use `ddl-auto: update` in v1 and defer Flyway.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `backend/src/main/java/com/entreck` | New/Modified | Event, POS, link, shared kernel, persistence, REST, and security scaffold |
| `backend/Dockerfile`, `docker-compose.yaml` | New | Temurin 21 backend and PostgreSQL 16 local runtime |
| `backend/src/test` | New | Unit, `@DataJpaTest`, `@WebMvcTest`, and smoke coverage |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Boot 4.1.0/Security 7 behavior changes | Medium | Pin versions and verify with smoke/controller tests |
| Write IDs are trusted without auth | High | Require a private network or authentication proxy before deployment |
| `ddl-auto: update` is unsafe for production evolution | High | Add Flyway in v1.1 before production |

## Rollback Plan

Revert the six chained slices independently; each slice has a rollback boundary recorded in `tasks.md`, leaving earlier domain/application slices usable when infrastructure slices are reverted.

## Dependencies

- Docker for PostgreSQL 16/Testcontainers and the pinned Spring Boot 4.1.0 dependency set.

## Success Criteria

- [ ] All 12 in-scope use cases are reachable through the 11 documented endpoints.
- [ ] Validation, duplicate, not-found, pagination, and availability behaviors have automated coverage.
- [ ] Local Docker and Testcontainers use PostgreSQL 16 without H2 or PostGIS.
