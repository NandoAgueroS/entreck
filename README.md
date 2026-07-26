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

Requirements: **JDK 21**, PostgreSQL 16 (when you wire the datasource).

```bash
cd backend
./mvnw compile
./mvnw test
./mvnw spring-boot:run
```

Prefer `./mvnw` over a system `mvn` so the Maven version stays pinned.

API will be versioned under `/api/v1` once endpoints exist.

## Mobile

Not scaffolded yet. Planned: Android, Kotlin, Jetpack Compose, MapLibre.
See `AGENTS.md` for conventions.

## License

TBD.
