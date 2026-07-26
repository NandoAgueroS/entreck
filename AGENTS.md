# AGENTS.md — Entreck

Guidance for AI coding agents working in this repository. Follow it exactly.

## 1. Project Overview

Entreck is a mobile-first directory (not a ticketing platform in v1) that centralizes
information about **physical points of sale** for **event tickets**. It helps users find
where to buy tickets near them, without relying on social media or WhatsApp threads.

- Users: ticket buyers, event organizers, and shops acting as points of sale.
- MVP scope: publish events, associate one or more points of sale, show them on a map,
  search events by name/category, search nearby points of sale, and display shop info
  (address, hours, contact, location).
- The app does **not** sell tickets in v1.

MVP use cases (actors, IDs, non-goals): see [`docs/use-cases.md`](docs/use-cases.md).
Keep code and comments aligned with that list and with the glossary in §6.

## 2. Repository Structure

Single monorepo. Backend and mobile live in the same repo under separate module roots.

```
entreck/
├── backend/        # Spring Boot (Clean Architecture)
├── app/            # Android (Jetpack Compose, MVVM + StateFlow)
├── docs/           # optional, only when explicitly requested
└── AGENTS.md
```

Each module owns its own build file — backend: `pom.xml` (Maven), app: `build.gradle.kts`
+ `settings.gradle.kts` (Gradle). They are independent builds under one repo. Each module
owns its own `README` and tests. Never cross-import source between `/backend` and `/app`.

## 3. Tech Stack

| Layer    | Technology                                   |
| -------- | -------------------------------------------- |
| Backend  | Spring Boot, Java 21                         |
| Database | PostgreSQL 16                               |
| Mobile   | Android, Kotlin, Jetpack Compose            |
| Maps     | MapLibre (GL Native / Compose Map SDK)      |
| API      | REST JSON (versioned under `/api/v1`)       |

Backend language is **Java 21**. Keep it consistent across the module; do not mix Kotlin
unless the user explicitly requests it.

Build tooling: the backend builds with **Maven Wrapper** (`backend/mvnw`); the Android app
builds with **Gradle**. They are independent build systems within the monorepo.
Always prefer `./mvnw` over a system-installed `mvn` so the Maven version stays pinned.

## 4. Backend Conventions (Clean Architecture)

Package layout under `com.entreck.<context>`:

```
domain/            # entities, value objects, repository PORTS (interfaces only)
application/       # use cases, application services, DTOs, mappers
infrastructure/    # JPA entities, repository ADAPTERS, Spring config, clients
interfaces/        # REST controllers, request/response models, exception handlers
```

Rules:

- `domain` has **no** Spring, JPA, or framework imports. It is pure Java (Java 21).
- Dependencies point inward: `interfaces` → `application` → `domain`; `infrastructure` → `domain`.
- Repository interfaces (ports) live in `domain`; implementations (adapters) live in `infrastructure`.
- Controllers only translate HTTP ↔ DTOs and delegate to use cases. No business logic in controllers.
- Use case classes are named `<Verb><Noun>UseCase` (e.g. `PublishEventUseCase`).
- One use case per interactor class; inject ports, not adapters.

## 5. Mobile Conventions (MVVM + StateFlow)

Package layout under `com.entreck.app`:

```
ui/                # Composable screens + stateless presentational components
ui/viewmodel/      # ViewModels exposing immutable UiState via StateFlow
domain/            # models, use cases, repository interfaces
data/              # repository implementations, remote (Retrofit/HTTP) + local sources
di/                # dependency injection (Hilt/Koin) modules
```

Rules:

- `ViewModel` exposes a single immutable `UiState` via `StateFlow` (read-only to the UI).
- UI collects state with `collectAsStateWithLifecycle()` and emits `UiEvent`s back to the ViewModel.
- No business logic inside Composables. Screens render from `UiState` and forward events.
- Map interactions (MapLibre) are handled in a dedicated map component; the ViewModel holds
  the list of `PointOfSale` markers as state, not the map object itself.
- Repository interfaces live in `domain`; HTTP/local implementations live in `data`.

## 6. Domain Glossary

Use these exact terms in code and comments:

- **Event**: a ticketed event (name, category, date, description).
- **PointOfSale** (POS): a physical shop authorized to sell tickets for one or more events.
  Has address, opening hours, contact, and geographic location.
- **Stock / Open status**: per-POS availability signal (v1 may be a simple flag; model it
  as data, not hardcoded).
- **Nearby search**: query points of sale within a radius of a user location.

## 7. Language & Style

- **All code, identifiers, comments, commit messages, and PR text are in English.**
- UI copy is internationalized (i18n): ship **English and Spanish** string resources
  (`values/strings.xml` + `values-es/strings.xml`). User-facing copy lives only in those
  resource files; never hardcode UI text in Composables.
- Formatting: backend follows standard Java conventions (Java 21); mobile follows the
  official **Kotlin** coding conventions.
- No AI attribution, no "Co-Authored-By" lines in commits.

## 8. Testing (Balanced)

Backend:

- Unit tests for domain and use cases (no Spring context).
- Basic integration tests for critical adapters (repository ↔ PostgreSQL) using
  **Testcontainers** so they run against a real Postgres in CI.

Mobile:

- Unit tests for ViewModels (state transitions, event handling) with Turbine or bare
  `StateFlow` assertions.
- No instrumented/Compose UI tests required for MVP unless explicitly requested.

Run backend tests with `./mvnw test` from `backend/`. Run mobile unit tests with
`./gradlew :app:testDebugUnitTest`. Do not claim tests pass without running them.
See §9 for the full command table.

## 9. Build & Verify Commands

Backend: **Maven Wrapper** from `backend/`. Mobile: **Gradle** from the app module root.
Do not use a bare `mvn` unless `./mvnw` is missing (it should not be).

| Action            | Backend (cwd: `backend/`)     | Mobile                        |
| ----------------- | ----------------------------- | ----------------------------- |
| Compile           | `./mvnw compile`              | `./gradlew :app:compileDebugKotlin` |
| Unit tests        | `./mvnw test`                 | `./gradlew :app:testDebugUnitTest` |
| Run               | `./mvnw spring-boot:run`      | `./gradlew :app:installDebug` |

## 10. Secrets & Environment

- PostgreSQL connection and the **MapLibre style/API key** are secrets. Never commit them.
- Backend: read DB config from environment variables or `application.yml` with placeholders.
- Mobile: store the MapLibre key in `local.properties` (`MAPIBRE_KEY=...`) and reference it
  from `build.gradle.kts`; add `local.properties` to `.gitignore`.
- `.gitignore` must exclude `local.properties`, `*.env`, build dirs, and IDE folders.

## 11. Git Workflow

- **Conventional Commits**: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`.
- Short-lived branches: `feature/<slug>`, `fix/<slug>`, based off `main`.
- One logical change per PR. Keep PRs focused and under ~400 changed lines when possible.
- PRs are opened with `gh` **only if the GitHub CLI is installed and authenticated**.
  The CLI is NOT guaranteed in this environment — if `gh` is unavailable, prepare the
  branch and commit, then tell the user to open the PR manually. Never fail silently on
  missing `gh`; report it and stop.
- Do not push or open a PR without explicit user request.
- Never amend, force-push, or create empty commits unless asked.

## 12. Agent Behavior

- Prefer small, reviewable changes over large rewrites.
- Before non-trivial changes, restate the plan in a few lines and confirm.
- Reuse existing modules and patterns; do not introduce new architecture styles on a whim.
- When a request is ambiguous, ask one focused question rather than guessing.
- Keep the domain terminology from Section 6 consistent across backend and app.
