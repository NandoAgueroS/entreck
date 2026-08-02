# Tasks: Backend Foundation

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~1,480 (280+200+250+180+220+350) |
| 400-line budget risk | High (slice 6 alone ≈ 350 lines) |
| Chained PRs recommended | Yes |
| Suggested split | PR 1 (bootstrap) → PR 2 (event-d) → PR 3 (event-ia) → PR 4 (pos-d) → PR 5 (pos-ia) → PR 6 (link) |
| Delivery strategy | ask-always |
| Chain strategy | feature-branch-chain (PR 1 → tracker; PR n → PR n-1 branch; tracker → main at end) |

Decision needed before apply: Yes
Chained PRs recommended: Yes
Chain strategy: feature-branch-chain
400-line budget risk: High

### Suggested Work Units

| Unit | Goal | Likely PR | Focused test command | Runtime harness | Rollback boundary |
|------|------|-----------|----------------------|-----------------|-------------------|
| 1 | Bootstrap: Docker/Testcontainers/kernel | PR 1 | `./mvnw test -Dtest=BackendApplicationSmokeTest` | `docker compose up` boots backend+postgres, `/actuator/health` UP | Revert PR 1 — no domain code exists yet |
| 2 | Event domain+app | PR 2 | `./mvnw test -Dtest='Event*Test,*UseCaseTest'` | N/A (no Spring, no HTTP — pure JUnit+Mockito) | Revert PR 2 — slice 1 still green alone |
| 3 | Event infra+API | PR 3 | `./mvnw test -Dtest=Event*IT,EventControllerWebMvcTest` | Docker for Testcontainers adapter IT | Revert PR 3 — use cases still testable without adapter |
| 4 | POS domain+app | PR 4 | `./mvnw test -Dtest='PointOfSale*Test,*PosUseCaseTest'` | N/A (pure JUnit+Mockito) | Revert PR 4 — event slices unaffected |
| 5 | POS infra+API | PR 5 | `./mvnw test -Dtest=PointOfSale*IT,PointOfSaleControllerWebMvcTest` | Docker for Testcontainers adapter IT | Revert PR 5 — POS use cases still testable alone |
| 6 | Link aggregate + cross-cutting | PR 6 | `./mvnw test` (full suite — cross-cutting) | Docker + full Spring context for GlobalExceptionHandler wiring | Revert PR 6 — event+POS slices remain independently green |

## Slice 1 — Bootstrap (~280 lines)

### infrastructure
- [x] **1.1.1** Add `org.testcontainers:postgresql` + `org.testcontainers:junit-jupiter` (test scope) to `backend/pom.xml` (Boot 4.1.0 BOM manages versions).
- [x] **1.1.2** Create `backend/Dockerfile` — multi-stage Temurin 21 build→JRE, non-root `spring` user, `/actuator/health` healthcheck (design §8.1).
- [x] **1.1.3** Create `docker-compose.yaml` at repo root — `db` (postgres:16) + `backend` services, named volume `entreck-pgdata`, `depends_on: condition: service_healthy` (design §8.2).
- [x] **1.1.4** Create `.env.example` with `POSTGRES_USER`/`POSTGRES_PASSWORD` placeholders.
- [x] **1.1.5** Update `backend/src/main/resources/application.yaml` — datasource env vars, `ddl-auto: update`, `hibernate.jdbc.time_zone: UTC`, actuator `health,info` + probes (design §8.4).
- [x] **1.1.6** Create `com.entreck.config.SecurityConfig` — `SecurityFilterChain` bean, csrf disabled, `anyRequest().permitAll()` (ADR-8).

### domain (shared kernel)
- [x] **1.2.1** Create `shared/domain/id/{EventId,PointOfSaleId,OrganizerId,ShopId}.java` — `record XId(Long value)` with null-check compact ctor (ADR-5).
- [x] **1.2.2** Create `shared/domain/enums/{AvailabilityStatus,EventCategory}.java` — AVAILABLE/LIMITED/SOLD_OUT/UNKNOWN; CONCERT/THEATER/SPORTS/CONFERENCE/FESTIVAL/OTHER (design §2.3).
- [x] **1.2.3** Create `shared/domain/PageMeta.java` (record: page, size, totalElements, totalPages) and `KernelException.java` (abstract unchecked base).

### interfaces (shared web)
- [x] **1.3.1** Create `shared/web/{PageResponse,ErrorResponse,FieldErrorItem}.java` — pagination envelope + error DTOs (design §3.4).
- [x] **1.3.2** Create `shared/web/GlobalExceptionHandler.java` — `@RestControllerAdvice` scaffold (400/404/422/500 mappings finalized in slice 6).

### tests
- [x] **1.4.1** Create `shared/testing/BaseIntegrationTest.java` — static `@Container PostgreSQLContainer<>("postgres:16")` + `@DynamicPropertySource` (design §8.3).
- [x] **1.4.2** Create `BackendApplicationSmokeTest.java` — `@SpringBootTest` asserts `/actuator/health` returns UP with Testcontainers.
- [x] **1.4.3** Verify `./mvnw test` passes — full suite green after bootstrap.

## Slice 2 — Event Domain + Application (~200 lines)

### domain
- [x] **2.1.1** Create `event/domain/Event.java` — aggregate root, state transitions `DRAFT→PUBLISHED→CANCELLED` via methods (design §2.2).
- [x] **2.1.2** Create `event/domain/EventStatus.java` — enum DRAFT/PUBLISHED/CANCELLED.
- [x] **2.1.3** Create `event/domain/repository/EventRepository.java` — port: `save`, `findById(EventId)`, `search(criteria, pageable)`, `findByName`, `existsByName`.
- [x] **2.1.4** Create `event/domain/EventSearchCriteria.java` — immutable search criteria record (name + category filters).

### application
- [x] **2.2.1** Create `event/application/usecase/{PublishEventUseCase,UpdateEventUseCase,FindEventsUseCase,GetEventDetailUseCase}.java` — one interactor per use case (O01, O02, B01/B02, B03).
- [x] **2.2.2** Create `event/application/dto/{CreateEventRequest,UpdateEventRequest,EventResponse,EventSummaryResponse,EventDetailResponse}.java` — DTOs with `@Valid` constraints.
- [x] **2.2.3** Create `event/application/exception/{EventNotFoundException,DuplicateEventNameException}.java` — extend `KernelException`.

### tests
- [x] **2.3.1** Create `EventTest.java` — aggregate invariants (name uniqueness semantics, valid status transitions, publish/cancel preconditions).
- [x] **2.3.2** Create `*UseCaseTest.java` per use case (4 files) — JUnit 5 + Mockito mocking `EventRepository`, AssertJ assertions.
- [x] **2.3.3** Verify `./mvnw test` passes — domain + application unit tests green (37 tests, 0 failures).

## Slice 3 — Event Infrastructure + API (~250 lines)

### infrastructure
- [ ] **3.1.1** Create `event/infrastructure/persistence/EventJpaEntity.java` — `@Entity` mapping to `events` table, all columns incl. `organizer_id`, `status` enum.
- [ ] **3.1.2** Create `event/infrastructure/persistence/SpringDataEventRepository.java` — `extends JpaRepository<EventJpaEntity, Long>` + derived queries (name+category+pageable).
- [ ] **3.1.3** Create `event/infrastructure/persistence/EventMapper.java` — static `toDomain`/`toJpaEntity` (ADR-3).
- [ ] **3.1.4** Create `event/infrastructure/persistence/EventRepositoryAdapter.java` — `@Repository`, implements `EventRepository` port via Spring Data repo.

### interfaces
- [ ] **3.2.1** Create `event/interfaces/EventController.java` — `@RestController` `/api/v1/events`: GET search (B01/B02), GET by id (B03), POST publish (O01), PATCH update (O02, ADR-13).
- [ ] **3.2.2** Add `@Valid` on request bodies + HTTP status mapping (201 for POST, 200 for GET/PATCH) per design §3.2.

### tests
- [ ] **3.3.1** Create `EventRepositoryAdapterIT.java` extends `BaseIntegrationTest` — `@DataJpaTest` verifies save/find/search pagination + unique-name conflict.
- [ ] **3.3.2** Create `EventControllerWebMvcTest.java` — `@WebMvcTest` with `@MockBean` use cases, verifies validation, 200/201/404/422 mapping, JSON shape.
- [ ] **3.3.3** Verify `./mvnw test` passes — adapter + controller tests green.

## Slice 4 — POS Domain + Application (~180 lines)

### domain
- [ ] **4.1.1** Create `pos/domain/PointOfSale.java` — aggregate root with typed value objects (design §2.2).
- [ ] **4.1.2** Create `pos/domain/POSStatus.java` — enum ACTIVE/INACTIVE.
- [ ] **4.1.3** Create `pos/domain/valueobject/{Address,GeoLocation,OpeningHours,WeeklyWindow,Contact}.java` — immutable records with compact-ctor validation (ADR-4, design §2.3: lat∈[-90,90], lon∈[-180,180], ISO-3166 country, RFC-5322-lite email, phone regex, open<close).
- [ ] **4.1.4** Create `pos/domain/repository/PointOfSaleRepository.java` — port: `save`, `findById`, `existsById`.

### application
- [ ] **4.2.1** Create `pos/application/usecase/{RegisterPointOfSaleUseCase,UpdatePointOfSaleUseCase,GetPointOfSaleDetailUseCase}.java` — one interactor per use case (S01, S02, B06).
- [ ] **4.2.2** Create `pos/application/dto/{CreatePointOfSaleRequest,UpdatePointOfSaleRequest,PointOfSaleResponse,PointOfSaleDetailResponse,AddressDto,GeoLocationDto,OpeningHoursDto,ContactDto}.java`.
- [ ] **4.2.3** Create `pos/application/exception/PointOfSaleNotFoundException.java` — extends `KernelException`.

### tests
- [ ] **4.3.1** Create `*ValueObjectTest.java` per VO — compact-ctor rejections (lat=100, empty street, bad email, open≥close).
- [ ] **4.3.2** Create `*UseCaseTest.java` per use case (3 files) — JUnit 5 + Mockito mocking `PointOfSaleRepository`.
- [ ] **4.3.3** Verify `./mvnw test` passes — POS unit tests green.

## Slice 5 — POS Infrastructure + API (~220 lines)

### infrastructure
- [ ] **5.1.1** Create `pos/infrastructure/persistence/PointOfSaleJpaEntity.java` — `@Entity` with `@Embedded` value objects (Address, GeoLocation, OpeningHours, Contact).
- [ ] **5.1.2** Create `pos/infrastructure/persistence/SpringDataPointOfSaleRepository.java` — extends `JpaRepository<PointOfSaleJpaEntity, Long>`.
- [ ] **5.1.3** Create `pos/infrastructure/persistence/PointOfSaleMapper.java` — static `toDomain`/`toJpaEntity` including value-object mapping.
- [ ] **5.1.4** Create `pos/infrastructure/persistence/PointOfSaleRepositoryAdapter.java` — `@Repository`, implements `PointOfSaleRepository` port.

### interfaces
- [ ] **5.2.1** Create `pos/interfaces/PointOfSaleController.java` — `@RestController` `/api/v1/points-of-sale`: GET detail (B06), POST register (S01), PATCH update (S02).
- [ ] **5.2.2** Add `@Valid` on nested `location.latitude`/`location.longitude` (field-level errors per design §3.3).

### tests
- [ ] **5.3.1** Create `PointOfSaleRepositoryAdapterIT.java` — `@DataJpaTest` with Testcontainers, verifies embedded VO round-trip + lat/lon range persisted.
- [ ] **5.3.2** Create `PointOfSaleControllerWebMvcTest.java` — `@WebMvcTest`, verifies lat/lon validation, 200/201/400/404 mapping.
- [ ] **5.3.3** Verify `./mvnw test` passes — POS adapter + controller green.

## Slice 6 — Event↔POS Link + Cross-cutting (~350 lines)

### domain
- [ ] **6.1.1** Create `shared/domain/link/EventPointOfSaleLink.java` — aggregate (belongs to event context per ADR-2), carries `eventId`, `posId`, `availabilityStatus`, `note`, `updatedAt`; single mutating method `changeAvailability(status, note, instant)`.
- [ ] **6.1.2** Create `shared/domain/link/LinkId.java` — `record LinkId(Long value)`.
- [ ] **6.1.3** Create `shared/domain/link/repository/EventPointOfSaleLinkRepository.java` — port: `save`, `findByEventId`, `findByEventIdAndPosId`, `existsByEventIdAndPosId`, `delete`.

### application
- [ ] **6.2.1** Create `event/application/usecase/{AssociatePosToEventUseCase,DissociatePosFromEventUseCase,ListEventPointOfSaleUseCase,UpdateAvailabilityUseCase}.java` — one interactor per use case (O03, O04, B04, S03). Injects `EventRepository`, `PointOfSaleRepository`, `EventPointOfSaleLinkRepository`.
- [ ] **6.2.2** Create `event/application/dto/{AssociateRequest,LinkedPosResponse,AvailabilityRequest,AvailabilityResponse}.java`.
- [ ] **6.2.3** Create `event/application/exception/LinkAlreadyExistsException.java` (+ LinkNotFoundException if not yet in shared) — extend `KernelException`.

### infrastructure
- [ ] **6.3.1** Create `event/infrastructure/persistence/EventPointOfSaleLinkJpaEntity.java` — `@Entity`, unique constraint `(event_id, pos_id)` (design §2.4).
- [ ] **6.3.2** Create `event/infrastructure/persistence/SpringDataEventPointOfSaleLinkRepository.java` + `EventPointOfSaleLinkMapper.java` + `EventPointOfSaleLinkRepositoryAdapter.java` (`@Repository`).

### interfaces
- [ ] **6.4.1** Add B04/O03/O04 to `event/interfaces/EventController.java`: GET `/{eventId}/points-of-sale`, POST `/{eventId}/points-of-sale`, DELETE `/{eventId}/points-of-sale/{posId}`.
- [ ] **6.4.2** Add S03 to `pos/interfaces/PointOfSaleController.java`: PUT `/{posId}/events/{eventId}/availability` (design §3.3).
- [ ] **6.4.3** Finalize `shared/web/GlobalExceptionHandler.java` — map `EventNotFoundException`→404, `PointOfSaleNotFoundException`→404, `LinkNotFoundException`→404, `LinkAlreadyExistsException`→422, `DuplicateEventNameException`→422, `MethodArgumentNotValidException`→400 with field errors, generic `Exception`→500 with correlation id (design §3.4).

### tests
- [ ] **6.5.1** Create `EventPointOfSaleLinkTest.java` — aggregate invariants (append-only except `changeAvailability`, status enum invariant).
- [ ] **6.5.2** Create `EventPointOfSaleLinkRepositoryAdapterIT.java` — `@DataJpaTest` verifies unique constraint, `changeAvailability` round-trip, cascade delete.
- [ ] **6.5.3** Create `*UseCaseTest.java` for 4 link use cases — mock all 3 ports.
- [ ] **6.5.4** Extend `EventControllerWebMvcTest` (B04/O03/O04), `PointOfSaleControllerWebMvcTest` (S03), add `GlobalExceptionHandlerTest` — verify 404/422/400/500 + correlation id header.
- [ ] **6.5.5** Verify `./mvnw test` passes — full suite green.

---

Total: ~43 tasks across 6 slices. ~1,480 estimated changed lines.
Slice 6 alone is ~350 lines (closest to 400-line budget — may need user size confirmation before apply).
Test class naming: `*Test` (unit, no Spring), `*IT` (adapter, Testcontainers), `*WebMvcTest` (controller, MockMvc).
