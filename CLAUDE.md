# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**DBS** (Chess24) is a multi-module Gradle/Kotlin microservices system built on Spring Boot 3.4.2 with reactive programming (WebFlux, R2DBC). Services communicate via gRPC and Kafka; REST endpoints are exposed via WebFlux.

## Build Commands

```bash
# Build all modules
./gradlew build

# Build a specific module
./gradlew :cc:mgmt:build

# Build runnable JARs (only for application modules with spring-boot plugin)
./gradlew :cc:mgmt:bootJar

# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Run tests for a specific module
./gradlew :cc:mgmt:test

# Run a single test class
./gradlew :cc:mgmt:test --tests "org.dbs.mgmt.SomeTest"

# Code coverage report
./gradlew testCoverage

# Static analysis
./gradlew detekt

# Code formatting check
./gradlew spotlessCheck

# Apply formatting
./gradlew spotlessApply
```

**JVM requirement:** Java 21+  
**Build JVM args:** `-Xmx3072m` (set in `gradle.properties`)

## Module Structure

Modules follow a strict layered pattern — each domain has sibling modules:

| Suffix | Purpose |
|---|---|
| *(none)* | Spring Boot application (runnable) |
| `-api` | Interfaces and domain model (no Spring) |
| `-proto-api` | Protobuf/gRPC contract definitions |
| `-grpc-client` | Generated gRPC client stubs |

**Domains:**
- `shared/` — Cross-cutting starter libraries (r2dbc, cache, kafka, security, mail, mongo, grpc)
- `auth/` — Authentication service (`auth-server`) and JWT verification (`auth-verify`)
- `cc/` — Chess Club: `mgmt` (management app), `sandbox`
- `tik/` — Tournament core
- `industrial/` — Goods/inventory
- `mail-server/` — Email service
- `out-of-service/` — Service availability management
- `p-cm/` — Project management (analyst, tasker)

**Shared infrastructure modules** under `shared/`:
- `spring-grpc-server-starter` / `spring-grpc-client-starter` — gRPC server/client auto-config
- `r2dbc-starter` — Reactive PostgreSQL setup
- `cache-starter` — Redis caching
- `kafka-api` / `spring-kafka-starter` — Kafka messaging
- `security-config-starter` — JWT-based Spring Security
- `ref-starter` — Reference data management

## Architecture

### Reactive Stack
All I/O is non-blocking: WebFlux for HTTP, R2DBC for PostgreSQL, reactive MongoDB driver, Lettuce for Redis. Business logic uses Project Reactor (`Mono`/`Flux`) and Kotlin coroutines where needed.

### Service Base Classes
- `AbstractApplicationService` — base with Reactor schedulers
- `AbstractApplicationBean` — beans with structured logging
- `DaoAbstractApplicationService` — DAO + service pattern
- Services are located via Spring's service locator pattern

### Inter-service Communication
- **gRPC** (port configured per service) for synchronous service-to-service calls
- **Kafka** for async events
- Protobuf contracts are defined in `-proto-api` modules and compiled during build

### Package Conventions
Within an application module (e.g., `cc/mgmt`):
- `org.dbs.<domain>.service` — business logic
- `org.dbs.<domain>.repo` — R2DBC repositories
- `org.dbs.<domain>.model` — domain entities
- `org.dbs.<domain>.config` — Spring configuration
- `org.dbs.<domain>.grpc` — gRPC service implementations

### Dependency Versions
All versions are centralized in two places:
- `buildSrc/src/main/kotlin/dsl/Dependencies.kt` — version constants
- `settings.gradle.kts` — Gradle version catalog

## Testing

Tests use **Kotest 5** (`BehaviorSpec` — Given/When/Then style) with JUnit Platform runner.

**Test infrastructure:**
- `AbstractChessTest` — base class; starts Spring Boot context with `WebEnvironment.RANDOM_PORT`
- **Testcontainers** spins up PostgreSQL (R2DBC), Kafka, and Redis per test run
- Dynamic property sources wire container URLs into Spring context
- JWT tokens are generated inline for authenticated test scenarios

**Test execution config (in `build.gradle.kts`):**
- Parallel: fork every 100 tests, max forks = `processors / 2`
- Retry: up to 10 retries for flaky tests
- JaCoCo minimum coverage: 50%

## Key Technology Versions

| Technology | Version |
|---|---|
| Kotlin | 2.1.0 |
| Spring Boot | 3.4.2 |
| Spring Cloud | 3.1.4 |
| gRPC | 1.66.0 |
| Protobuf | 4.29.3 |
| Kotest | 5.9.1 |
| Testcontainers | 1.20.4 |
| Gradle | 8.14.4 |

## SSL/TLS

Certificates from `certs/k11dev.tech/` (JKS, PEM, private keys) are automatically copied into each module's resources during build. Do not commit new certs directly to module `resources/` directories — update the source in `certs/`.

## Docker / Kubernetes

- `docker-compose/` — compose templates for `cc` and `industrial` domains
- `k8s/` — Kubernetes manifest templates

Spring profiles: `development`, `test`, `production`. Secrets (JWT keys, DB URLs, broker URLs) are injected via environment variables.

## Other requirements and conventions:

 /ai/conventions/database.md