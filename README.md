# Entreck

Mobile-first **directory** of physical points of sale for event tickets.
Find where to buy tickets near you — the app does **not** sell tickets in v1.

## Repository layout

```
entreck/
├── backend/     # Spring Boot API (Java 21, Maven Wrapper)
├── app/         # Android app (planned: Kotlin, Jetpack Compose)
├── docs/        # Product docs (use cases, etc.)
├── AGENTS.md    # Conventions for humans and coding agents
└── README.md
```

Backend and mobile are **independent builds** in one monorepo. Do not cross-import source between modules.

## Docs

| Doc | What it covers |
| --- | --- |
| [AGENTS.md](AGENTS.md) | Stack, architecture, testing, git, agent rules |
| [docs/use-cases.md](docs/use-cases.md) | MVP actors, use cases, product decisions |

## Backend (quick start)

Requirements: **JDK 21**, **Docker + Docker Compose** for the local database.

### Run with Docker

```bash
cp .env.example .env
# Edit .env with real values, then:
docker compose up
```

The backend container builds from `./backend/Dockerfile` and connects to the
`postgres:16` service. Health checks are exposed at `/actuator/health`.

### Run locally for development

```bash
cd backend
./mvnw compile
./mvnw test
./mvnw spring-boot:run
```

Prefer `./mvnw` over a system `mvn` so the Maven version stays pinned.

The local database is provided by Testcontainers during tests; no manual
PostgreSQL installation is required for `./mvnw test`.

### v1 security trust boundary

All endpoints are public in v1. The baseline `SecurityFilterChain` permits all
requests so the API can be deployed behind a private network or an auth proxy.
JWT-based authentication is planned for v1.1.

API endpoints are versioned under `/api/v1` once slices 2-6 land.

## Mobile

Not scaffolded yet. Planned: Android, Kotlin, Jetpack Compose, MapLibre.
See `AGENTS.md` for conventions.

## License

TBD.
