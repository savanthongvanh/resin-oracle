# Resin Oracle Service Documentation

This document is the operator-facing runbook for the current repo state.

## What this project is

- A containerized Java 8 service running on Resin and backed by Oracle Free 23c.
- The build and CI/CD path is Gradle via the Gradle Wrapper.
- The application exposes a REST endpoint that looks up US state data by abbreviation.
- The repository includes a runnable Docker Compose stack for local endpoint testing.
- One of the main design goals is to demonstrate that datasource URL, credentials, and connection-pool sizing are externalized into Resin runtime configuration instead of application code.
- The Resin-managed pool is backed by `oracle.jdbc.pool.OracleDataSource` from the `ojdbc8` runtime dependency.

## Local setup

- Required tools:
  - Docker Desktop or Docker Engine with Compose support
  - optional local Java 8+ if you want to run Gradle directly on the host
- Secret source:
  - `env.dev`, loaded by Docker Compose and Resin runtime configuration
- Start the full stack:
  - `docker compose --env-file env.dev up -d --build`
- Stop the stack:
  - `docker compose --env-file env.dev down`
- Host build and CI/CD entrypoint:
  - `./gradlew clean test war`
- Java-free local build fallback:
  - `docker run --rm -u $(id -u):$(id -g) -v "$PWD":/workspace -w /workspace gradle:7.6.4-jdk8 ./gradlew --no-daemon clean test war copyRuntimeLibs`

## Verification commands

- test -f prompt.md
- test -f plans.md
- test -f implement.md
- test -f documentation.md
- test -f specification.md
- test -f docs/architecture/README.md
- test -f docs/architecture/application-layering.mmd
- test -f docs/architecture/application-layering.png
- docker compose --env-file env.dev ps
- curl -i http://localhost:8080/api/v1/states/TX
- curl -i http://localhost:8080/api/v1/states/TEX
- curl -i http://localhost:8080/api/v1/states/ZZ
- ./scripts/render-architecture-diagrams.sh
- docker run --rm -u $(id -u):$(id -g) -v "$PWD":/workspace -w /workspace gradle:7.6.4-jdk8 ./gradlew --no-daemon clean test war copyRuntimeLibs
- docker run --rm --add-host host.docker.internal:host-gateway -v "$PWD/postman":/etc/newman postman/newman:6-alpine run /etc/newman/resin-oracle.postman_collection.json -e /etc/newman/resin-oracle.postman_environment.json --env-var baseUrl=http://host.docker.internal:8080

## Demo or acceptance recipes

1. Start the stack with `docker compose --env-file env.dev up -d --build`.
2. Call `GET /api/v1/states/TX` and confirm a `200` JSON response with Texas data.
3. Call `GET /api/v1/states/TEX` and confirm `400 Bad Request`.
4. Call `GET /api/v1/states/ZZ` and confirm `404 Not Found`.
5. Show that the application only does a JNDI lookup in [DataSourceConfig.java](/Users/savanthongvanh/workspaces/resin-oracle/src/main/java/com/example/resinoracle/config/DataSourceConfig.java).
6. Show that JDBC URL, credentials, pool limits, and datasource class `oracle.jdbc.pool.OracleDataSource` are defined in [resin.xml.template](/Users/savanthongvanh/workspaces/resin-oracle/docker/resin/resin.xml.template) and substituted by [docker-entrypoint.sh](/Users/savanthongvanh/workspaces/resin-oracle/docker/resin/docker-entrypoint.sh).
7. Open [docs/architecture/README.md](/Users/savanthongvanh/workspaces/resin-oracle/docs/architecture/README.md) for the application-layering quick view and its screenshot.

## Repo structure overview

- [prompt.md](/Users/savanthongvanh/workspaces/resin-oracle/prompt.md): mission brief and hard requirements
- [plans.md](/Users/savanthongvanh/workspaces/resin-oracle/plans.md): milestone plan, verification gates, risks, and decisions
- [implement.md](/Users/savanthongvanh/workspaces/resin-oracle/implement.md): execution contract for future implementation
- [documentation.md](/Users/savanthongvanh/workspaces/resin-oracle/documentation.md): current operator runbook
- [specification.md](/Users/savanthongvanh/workspaces/resin-oracle/specification.md): implementation-ready system specification
- [docs/architecture/README.md](/Users/savanthongvanh/workspaces/resin-oracle/docs/architecture/README.md): architecture diagrams and screenshot-backed quick views
- [docs/architecture/application-layering.mmd](/Users/savanthongvanh/workspaces/resin-oracle/docs/architecture/application-layering.mmd): Mermaid source for the application-layering diagram
- [docs/architecture/application-layering.png](/Users/savanthongvanh/workspaces/resin-oracle/docs/architecture/application-layering.png): screenshot asset for the application-layering diagram
- [docker-compose.yml](/Users/savanthongvanh/workspaces/resin-oracle/docker-compose.yml): local runtime topology for Oracle and Resin
- [docker/resin/resin.xml.template](/Users/savanthongvanh/workspaces/resin-oracle/docker/resin/resin.xml.template): externalized datasource and pool configuration template
- [src/main/java/com/example/resinoracle/config/DataSourceConfig.java](/Users/savanthongvanh/workspaces/resin-oracle/src/main/java/com/example/resinoracle/config/DataSourceConfig.java): application-side JNDI datasource lookup only
- [postman/resin-oracle.postman_collection.json](/Users/savanthongvanh/workspaces/resin-oracle/postman/resin-oracle.postman_collection.json): endpoint integration tests
- [scripts/render-architecture-diagrams.sh](/Users/savanthongvanh/workspaces/resin-oracle/scripts/render-architecture-diagrams.sh): refreshes screenshot assets from Mermaid sources

## High-level data or file format notes

- `specification.md` is the authoritative technical design for the shipped stack.
- `env.dev` is the designated local secret file for generated credentials and runtime values.
- The application does not contain pool sizing or database credentials; those are injected into Resin at container startup from `env.dev`.
- Resin pools `oracle.jdbc.pool.OracleDataSource`, and the Oracle JDBC implementation is supplied by the `ojdbc8` runtime jar.
- Architecture diagrams live in `docs/architecture/`; Mermaid source is paired with committed screenshot assets.
- The API contract is JSON over HTTP with Oracle-backed state reference data.

## Troubleshooting

- If the application starts but cannot reach the database, inspect [env.dev.template](/Users/savanthongvanh/workspaces/resin-oracle/env.dev.template) and confirm the live `env.dev` values for `ORACLE_HOST`, `ORACLE_SERVICE_NAME`, `APP_DB_USER`, and `APP_DB_PASSWORD`.
- If you need to prove the exact datasource implementation, inspect [resin.xml.template](/Users/savanthongvanh/workspaces/resin-oracle/docker/resin/resin.xml.template): Resin configures `oracle.jdbc.pool.OracleDataSource` with an Oracle thin JDBC URL, and the runtime jar comes from `ojdbc8`.
- If you need to prove pool externalization, compare [DataSourceConfig.java](/Users/savanthongvanh/workspaces/resin-oracle/src/main/java/com/example/resinoracle/config/DataSourceConfig.java) with [resin.xml.template](/Users/savanthongvanh/workspaces/resin-oracle/docker/resin/resin.xml.template): the app only resolves JNDI, while Resin owns URL, credentials, datasource class, and pool limits.
- Resin may log warnings about `proxy-cache` requiring Resin Professional and a missing native `resin_os` library. In this local setup those warnings are non-fatal.
- If `env.dev` is created later, protect it locally and exclude it from version control.
