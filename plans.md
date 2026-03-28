# Resin Oracle Service Implementation Plan

This document is the execution plan, verification ledger, and decision log. `plans.md` is authoritative.

## Verification checklist (kept current)

Core commands:
test -f prompt.md
test -f plans.md
test -f implement.md
test -f documentation.md
test -f specification.md
test -f docs/architecture/README.md
test -f docs/architecture/application-layering.mmd
test -f docs/architecture/application-layering.png
rg -n "OpenJDK 8|Resin|Oracle Free|connection pool|env.dev|Gradle" specification.md

Last verified:

- [x] Initial control-plane scaffold created on 2026-03-27
- [x] Project-specific specification drafted in `specification.md`
- [x] Gradle CI/CD requirement added to the specification and plan
- [x] Agent-ready parallel stream plan documented with frozen contracts and write scopes
- [x] Implementation scaffolding created
- [x] Runnable Docker Compose stack exists for local endpoint testing
- [x] Docker stack starts successfully
- [x] REST endpoint returns seeded state data from Oracle Free
- [x] Resin-managed connection pool is wired through external runtime configuration
- [x] Application-layering architecture diagram and screenshot committed
- [x] JUnit and Postman/Newman verification re-run after the latest documentation pass

Final validation sweep:

- [x] test -f prompt.md
- [x] test -f plans.md
- [x] test -f implement.md
- [x] test -f documentation.md
- [x] test -f specification.md
- [x] test -f docs/architecture/README.md
- [x] test -f docs/architecture/application-layering.mmd
- [x] test -f docs/architecture/application-layering.png
- [x] rg -n "GET /api/v1/states/{abbreviation}" specification.md
- [x] rg -n "Resin-managed JNDI|externalized|Resin runtime configuration" specification.md documentation.md prompt.md implement.md
- [x] rg -n "env.dev" specification.md documentation.md
- [x] rg -n "Gradle|gradlew|war" specification.md documentation.md
- [x] ./scripts/render-architecture-diagrams.sh
- [x] docker compose --env-file env.dev up --build -d
- [x] curl -i http://localhost:8080/api/v1/states/TX
- [x] docker run --rm --add-host host.docker.internal:host-gateway -v "$PWD/postman":/etc/newman postman/newman:6-alpine run /etc/newman/resin-oracle.postman_collection.json -e /etc/newman/resin-oracle.postman_environment.json --env-var baseUrl=http://host.docker.internal:8080

## Parallel execution strategy

The plan is optimized for concurrent execution by multiple agents. Shared contracts are frozen early, write scopes stay disjoint by default, and convergence happens only at explicit gates.

### Foundation gate

Before parallel work begins, these contracts are frozen unless an integration gate explicitly approves a change:

- Endpoint contract: `GET /api/v1/states/{abbreviation}` with `200`, `400`, and `404`
- Response payload contract: abbreviation, name, capital, region, `updatedAt`
- Build contract: Gradle Wrapper, `war` packaging, `./gradlew clean test war`, `./gradlew check`, `./gradlew newmanTest`
- Runtime outcome contract: `docker compose --env-file env.dev up --build` produces a runnable local stack for endpoint testing
- Datasource contract: JNDI name `jdbc/oracle/states`
- Environment contract: `env.dev` contains Oracle and app credentials plus pool settings
- Repository port contract: service depends on a port or interface, not a concrete Oracle repository
- Pool externalization contract: JDBC URL, credentials, and pool sizing live in Resin runtime configuration, not in application code
- Architecture diagram contract: the repository ships a quick-view application-layering Mermaid diagram with a committed screenshot

### Agent-ready workstreams

1. Stream A - Build and CI/CD foundation
   - Write scope: `build.gradle`, `settings.gradle`, `gradlew`, `gradlew.bat`, `gradle/wrapper/**`, CI config
   - Deliverables: Java 8 WAR build plus `test`, `war`, `check`, and `newmanTest` task entrypoints

2. Stream B - Web and API contract
   - Write scope: `src/main/java/.../web/**`, `src/main/java/.../dto/**`, `src/test/java/.../web/**`
   - Deliverables: controller, validation, error mapping, DTOs, controller tests with mocked service

3. Stream C - Domain and service logic
   - Write scope: `src/main/java/.../service/**`, `src/main/java/.../domain/**`, `src/test/java/.../service/**`
   - Deliverables: normalization, orchestration, repository port, service tests

4. Stream D - Data and Oracle implementation
   - Write scope: `src/main/java/.../repository/**`, `db/**`, `scripts/init-db.sh`, `src/test/java/.../repository/**`
   - Deliverables: schema, seed data, Oracle repository implementation, row-mapping tests

5. Stream E - Resin and container platform
   - Write scope: `docker-compose.yml`, `Dockerfile.app`, `docker/resin/**`, `src/main/webapp/WEB-INF/**`, `.gitignore`, `env.dev.template`
   - Deliverables: Oracle container path, Resin deployment path, JNDI datasource and pool wiring, env-file conventions, runnable local Docker stack

6. Stream F - Verification and operator experience
   - Write scope: `postman/**`, `documentation.md`, verification scripts, Gradle wrappers for Newman
   - Deliverables: Postman collection, Newman assertions, operator runbook, screenshot-backed architecture diagrams, CI-facing verification notes

### Convergence rules

- Stream B and Stream C integrate only on controller-to-service boundaries after DTOs and service method signatures are frozen.
- Stream C and Stream D integrate only on the repository port after the interface is committed.
- Stream A and Stream F integrate only on Gradle task names after those entrypoints are committed.
- Stream E integrates with Streams A and D only on artifact path, datasource JNDI name, and env variable names after those contracts are written down.

### Integration gates

- Gate 1: Contract freeze complete and stream ownership assigned
- Gate 2: Streams A, B, C, and E compile and package a deployable WAR with stubbed or mocked data boundaries
- Gate 3: Streams C, D, and E connect the application to Oracle through the Resin-managed datasource
- Gate 4: Streams A, B, D, E, and F pass Gradle, Docker Compose, JUnit, and Newman verification together

## Milestones

### Milestone 01 - Planning and specification [x]

Scope:

- Bootstrap the long-horizon control documents.
- Draft the implementation-ready system specification.
- Resolve ambiguous requirements into explicit assumptions.

Key files/modules:

- `prompt.md`
- `plans.md`
- `implement.md`
- `documentation.md`
- `specification.md`

Acceptance criteria:

- The repository contains the four long-horizon control documents.
- The specification defines runtime, container topology, API contract, data model, pooling strategy, testing plan, secret handling, and Gradle CI/CD approach.
- The specification defines the architecture-diagram requirement and the quick-view layering expectation.
- Ambiguities are recorded explicitly instead of left implicit.

Verification commands:

- test -f prompt.md
- test -f plans.md
- test -f implement.md
- test -f documentation.md
- test -f specification.md
- rg -n "OpenJDK 8|Resin|Oracle Free|connection pool|env.dev|Gradle" specification.md

### Milestone 02 - Contract freeze and agent kickoff [x]

Scope:

- Lock the shared contracts that enable concurrent execution.
- Assign write scopes so streams can progress independently.

Key files/modules:

- `plans.md`
- `specification.md`
- `documentation.md`

Acceptance criteria:

- Shared contracts for API, datasource, env vars, Gradle task names, repository port, and pool externalization are explicit.
- The architecture diagram contract is explicit.
- The runtime outcome contract for a runnable local Docker endpoint-test stack is explicit.
- Every parallel stream has a disjoint default write scope.
- Agent handoff points and convergence rules are documented before broad implementation.

Verification commands:

- rg -n "Endpoint contract|Build contract|Runtime outcome contract|Datasource contract|Environment contract|Repository port contract|Pool externalization contract" plans.md
- rg -n "Stream A|Stream B|Stream C|Stream D|Stream E|Stream F" plans.md

### Milestone 03 - Foundation streams in parallel [x]

Scope:

- Establish Gradle, Docker, Oracle, Resin, API skeleton, and test harness scaffolding in parallel.

Key files/modules:

- `build.gradle`
- `settings.gradle`
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/*`
- `docker-compose.yml`
- `Dockerfile.app`
- `.gitignore`
- `env.dev.template`
- `src/main/java/.../web/StateController.java`
- `src/test/java/...`
- `postman/resin-oracle.postman_collection.json`

Acceptance criteria:

- Stream A: Gradle Wrapper builds a Java 8 WAR artifact and exposes `test`, `war`, and `check` tasks.
- Stream B: Web and DTO layers compile independently of database implementation.
- Stream C: Service layer and repository port compile independently of the concrete Oracle repository.
- Stream D: Schema and seed plan are committed far enough to unblock repository work.
- Stream E: Docker Compose, Oracle sourcing, and Resin base configuration are documented and reviewable.
- Stream E: Container design is sufficient to produce a runnable local stack once application and datasource pieces converge.
- Stream F: Test harness skeletons exist for JUnit and Postman/Newman.

Verification commands:

- ./gradlew tasks
- ./gradlew clean test war
- docker compose config
- test -f env.dev || test -f env.dev.template

### Milestone 04 - Feature streams in parallel [x]

Scope:

- Implement the first end-to-end feature slice while keeping streams disjoint.
- Complete controller, service, Oracle repository, schema, and pool wiring around the frozen contracts.

Key files/modules:

- `src/main/java/.../web/StateController.java`
- `src/main/java/.../web/StateExceptionHandler.java`
- `src/main/java/.../dto/**`
- `src/main/java/.../service/StateService.java`
- `src/main/java/.../domain/**`
- `src/main/java/.../repository/StateRepositoryPort.java`
- `src/main/java/.../repository/oracle/OracleStateRepository.java`
- `src/main/java/.../config/WebConfig.java`
- `src/main/java/.../config/DataSourceConfig.java`
- `src/main/webapp/WEB-INF/web.xml`
- `db/init/001_create_schema.sql`
- `db/init/002_seed_states.sql`
- `scripts/init-db.sh`

Acceptance criteria:

- Stream B: `GET /api/v1/states/{abbreviation}` returns a JSON payload for known states.
- Stream B: Invalid abbreviations return `400` and unknown abbreviations return `404`.
- Stream C: Service logic depends only on the repository port, not the concrete Oracle repository.
- Stream D: Oracle contains the application schema and deterministic seed data.
- Stream D: Oracle repository implementation satisfies the repository port contract.
- Stream E: Resin owns the Oracle datasource and JNDI pool configuration.
- Stream E: Pool URL, credentials, and sizing are externalized into Resin runtime configuration rather than application code.
- Stream F: Postman and JUnit suites target the frozen endpoint contract.

Verification commands:

- ./gradlew test
- curl -i http://localhost:8080/api/v1/states/TX
- curl -i http://localhost:8080/api/v1/states/ZZ
- curl -i http://localhost:8080/api/v1/states/TEX

### Milestone 05 - Integration and pool enforcement [x]

Scope:

- Converge the parallel streams into the first integrated build.
- Prove the deployed application uses only the Resin-managed pool and runs cleanly in containers.

Key files/modules:

- `docker/resin/resin.xml.template`
- `docker/resin/docker-entrypoint.sh`
- `src/main/java/.../config/DataSourceConfig.java`
- `src/main/java/.../repository/StateRepositoryPort.java`
- `src/main/java/.../repository/oracle/OracleStateRepository.java`
- `docker-compose.yml`
- `build.gradle`

Acceptance criteria:

- Resin owns the pool configuration and credentials resolution.
- Application code uses only the injected `DataSource`.
- No use of `DriverManager.getConnection` exists in application code.
- Pool configuration is demonstrably externalized from the application into Resin startup configuration and `env.dev`.
- The WAR produced by Gradle deploys into Resin through the container build.
- `docker compose --env-file env.dev up --build` produces a runnable application and database stack for endpoint testing.

Verification commands:

- rg -n "DriverManager\\.getConnection" src/main/java
- rg -n "JndiObjectFactoryBean|JndiTemplate|@Resource" src/main/java
- rg -n "DB_POOL_|APP_DB_PASSWORD|jdbc:oracle:thin" docker/resin docker-compose.yml env.dev.template
- ./gradlew clean test war
- docker compose --env-file env.dev up --build -d
- docker compose --env-file env.dev logs resin-app
- curl -i http://localhost:8080/api/v1/states/TX

### Milestone 06 - CI/CD validation and operator finish [x]

Scope:

- Finalize automated tests, Gradle-driven CI/CD entrypoints, and operator documentation.
- Make the build reproducible for both local and CI execution.

Key files/modules:

- `src/test/java/...`
- `postman/resin-oracle.postman_collection.json`
- `postman/resin-oracle.postman_environment.json`
- `documentation.md`
- `docs/architecture/**`
- `scripts/render-architecture-diagrams.sh`

Acceptance criteria:

- JUnit tests cover success, validation failure, and not-found cases.
- Postman tests validate the deployed endpoint contract.
- Gradle Wrapper is the documented CI/CD entrypoint.
- Runbook instructions match the actual implementation.
- The repo includes a Mermaid application-layering diagram with an accompanying screenshot.
- The verification flow can be executed without collapsing the stream model back into one manual process.
- The final build artifacts and Compose definitions produce a runnable Docker-based endpoint-test environment.

Verification commands:

- ./gradlew check
- ./gradlew newmanTest
- newman run postman/resin-oracle.postman_collection.json -e postman/resin-oracle.postman_environment.json
- test -f docs/architecture/README.md
- test -f docs/architecture/application-layering.mmd
- test -f docs/architecture/application-layering.png
- ./scripts/render-architecture-diagrams.sh

## Agent assignment template

Use this template when multiple agents are available:

- Agent 1 owns Stream A only.
- Agent 2 owns Stream B only.
- Agent 3 owns Stream C only.
- Agent 4 owns Stream D only.
- Agent 5 owns Stream E only.
- Agent 6 owns Stream F only.

If fewer agents are available, combine Streams B and C last, and combine Streams D and E only after the Oracle image path is decided. Do not combine Stream A with application logic unless staffing is constrained.

## Risk register

1. Oracle runtime portability

- Risk: free Oracle container support differs across host architectures, especially on Apple Silicon.
- Mitigation: standardize the local stack on `gvenzl/oracle-free:23-slim`, keep the application Oracle-compatible, and record the runtime choice explicitly in the spec and runbook.

2. Legacy platform compatibility

- Risk: Resin and Java 8 constrain library choices and exclude newer Spring Boot defaults.
- Mitigation: package the application as a WAR, use Spring Framework 5.3.x directly, and avoid embedded-container assumptions.

3. Connection leak or pool bypass

- Risk: later edits may introduce direct JDBC connection creation or fail to close pooled connections.
- Mitigation: enforce JNDI `DataSource` usage, scan for forbidden APIs, use try-with-resources, and verify the pool boundary in docs and runtime config.

4. Response contract drift

- Risk: consumers may later want a smaller payload than the canonical record currently returned.
- Mitigation: keep the controller and DTO boundaries isolated so payload adjustments do not force data-source or container changes.

5. Parallel stream merge friction

- Risk: parallel work can drift in contracts and create avoidable integration churn.
- Mitigation: keep the API contract, Gradle build contract, datasource contract, and pool externalization contract explicit in `specification.md` and this plan.

## Demo or acceptance flow

0:00-0:30

- Show `docker compose --env-file env.dev up --build` bringing up Oracle Free and the Resin application.

0:30-1:30

- Call `GET /api/v1/states/TX` and show the canonical JSON response from Oracle-backed data.

1:30-2:15

- Call invalid and unknown inputs to show `400` and `404` behavior.

2:15-3:00

- Show the JNDI lookup in application code and the externalized pool settings in `docker/resin/resin.xml.template`.
- Show the automated JUnit and Postman verification commands.

## Architecture or approach overview

### Core model

- One Oracle-backed read service centered on a `US_STATES` reference table.
- One public read-only endpoint for state lookup by abbreviation.
- One Resin-managed JNDI `DataSource` shared by the application.

### Execution model

- Docker Compose orchestrates the Oracle Free container and the Resin-hosted Java WAR.
- Resin defines the connection pool and exposes a JNDI `DataSource`.
- Spring MVC handles HTTP routing, the service layer enforces input normalization, and the repository layer performs JDBC reads through the injected pooled `DataSource`.
- The application code never carries JDBC URL, credentials, or pool sizes; those values are injected into Resin at container startup.

### Validation strategy

- Keep the spec and docs verifiable with grep-based checks.
- Validate build output, container startup, API behavior, and integration tests at every milestone.

## Implementation notes and decision log

- 2026-03-27: Selected a WAR deployment model on Resin instead of Spring Boot's embedded server model because the app server is a hard requirement.
- 2026-03-27: Proposed `GET /api/v1/states/{abbreviation}` as the canonical REST contract and defined `400` and `404` semantics.
- 2026-03-27: Chose Resin-managed JNDI datasource wiring as the only allowed database access path for the application.
- 2026-03-27: Added Gradle Wrapper as the required build and CI/CD path and reorganized the plan around parallel implementation streams.
- 2026-03-27: Implemented the runnable local stack with `gvenzl/oracle-free:23-slim` because it is practical on Docker Desktop arm64 while preserving the Oracle-compatible application contract.
- 2026-03-27: Excluded Washington, DC from the first dataset and seeded the 50 US states only.
- 2026-03-27: Externalized database URL, credentials, and pool settings into `docker/resin/resin.xml.template` plus container-startup substitution so the WAR demonstrates app-server-managed pooling instead of application-managed configuration.
- 2026-03-27: Re-verified the stack after documentation updates with a Dockerized Gradle build, direct Newman execution, a full Compose rebuild, and live endpoint checks.
- 2026-03-27: Added a screenshot-backed Mermaid application-layering diagram under `docs/architecture/` so the repo has a quick visual composition view.
- 2026-03-27: Added `scripts/render-architecture-diagrams.sh` to regenerate PNG screenshots from committed Mermaid sources.
- 2026-03-27: Clarified in docs and diagrams that Resin pools `oracle.jdbc.pool.OracleDataSource`, with the Oracle JDBC implementation supplied by `ojdbc8`.
