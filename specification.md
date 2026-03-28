# Resin Oracle Service Specification

## 1. Objective

Define the implementation specification for a containerized Java service with these required characteristics:

- OpenJDK 8 runtime
- Resin application server
- Oracle free-edition database tier, implemented in this repository with Oracle Free 23c
- Spring Framework libraries for application structure and database access
- Gradle-based build and CI/CD execution
- A runnable local Docker Compose stack for endpoint testing
- A RESTful endpoint that accepts a state abbreviation and returns state data
- Resin-managed JDBC connection pooling as the only database access path used by the application
- Pool configuration externalized from application code into Resin runtime configuration
- Architecture diagrams for the implementation, with Mermaid source allowed but accompanied by committed screenshots
- Postman endpoint tests and JUnit unit tests
- Generated credentials captured in `env.dev`

## 2. Scope

In scope:

- Local developer stack using Docker
- Runnable local container startup for endpoint testing
- One Oracle-backed REST lookup service
- One state lookup endpoint
- Resin datasource and connection pool configuration
- Architecture documentation for quick composition review
- Unit and API test strategy
- Gradle-based packaging and CI/CD task design
- Secret handling expectations for local development

Out of scope:

- Endpoint authentication and authorization
- Multi-region deployment
- Horizontal scaling beyond a single local application container
- Oracle production sizing or enterprise licensing

## 3. Assumptions and decisions

### 3.1 Oracle edition

- The phrase "oracle community version" is interpreted as Oracle's free database editions.
- This implementation uses `gvenzl/oracle-free:23-slim` for local development because it is a practical free Oracle runtime on Docker Desktop arm64 hosts.
- The application contract remains Oracle JDBC plus Oracle-compatible SQL, so the switch from XE to Oracle Free does not change the application design.

### 3.2 Application server and packaging

- The application server target is Resin 4.0.x.
- The Java application will be packaged as a WAR and deployed into Resin, not shipped as an embedded-server executable jar.
- OpenJDK 8 is the runtime and compilation target.

### 3.3 Spring usage

- Use Spring Framework 5.3.x modules directly instead of Spring Boot.
- Minimum planned modules:
  - `spring-context`
  - `spring-webmvc`
  - `spring-jdbc`
  - `spring-tx`
  - `spring-test`

### 3.4 Build and CI/CD decision

- Gradle is the required build automation tool.
- The repository will commit the Gradle Wrapper so local development and CI/CD use the same entrypoint.
- CI/CD must invoke Gradle tasks rather than Maven goals.

### 3.5 API payload assumption

- The endpoint requirement did not define the exact response body.
- The default response will be a canonical state record containing abbreviation, state name, capital, region, and last-updated timestamp.

## 4. Target architecture

### 4.1 Runtime topology

The local stack uses Docker Compose with two primary services:

1. `oracle-db`
   - Runs Oracle Free 23c (`gvenzl/oracle-free:23-slim`)
   - Stores persistent database files on a named Docker volume
   - Exposes Oracle listener port `1521`
   - Initializes the application schema and seed data

2. `resin-app`
   - Runs OpenJDK 8 and Resin
   - Deploys a WAR file for the state lookup application
   - Resolves Oracle connectivity values from `env.dev`
   - Owns the JDBC connection pool through Resin configuration

Runtime outcome requirement:

- The repository must produce a local Docker Compose stack that can be started by an engineer and used immediately for endpoint testing with curl, Postman, or Newman.

Optional implementation-time support services:

- A one-shot schema initialization step if Oracle image startup cannot own schema bootstrap cleanly
- Newman in CI for Postman execution
- Gradle tasks as the CI/CD control surface for compile, test, package, and endpoint verification

### 4.2 Request flow

1. Client sends `GET /api/v1/states/{abbreviation}`.
2. Resin routes the request to the deployed WAR.
3. Spring MVC controller normalizes and validates the abbreviation.
4. Service layer requests state data from the repository.
5. Repository obtains a connection from the Resin-managed JNDI `DataSource`.
6. Repository queries Oracle and maps the row to a response DTO.
7. Controller returns JSON with `200`, `400`, or `404` depending on the outcome.

## 5. API specification

### 5.1 Endpoint

- Method: `GET`
- Path: `/api/v1/states/{abbreviation}`
- Media type: `application/json`

### 5.2 Path parameter

- Name: `abbreviation`
- Allowed format: exactly two alphabetic characters
- Normalization: incoming values are uppercased before lookup

### 5.3 Success response

Status: `200 OK`

```json
{
  "abbreviation": "TX",
  "name": "Texas",
  "capital": "Austin",
  "region": "South",
  "updatedAt": "2026-03-27T00:00:00Z"
}
```

### 5.4 Client error responses

Invalid input format:

- Status: `400 Bad Request`
- Example triggers:
  - `TEX`
  - `1X`
  - empty value

Not found:

- Status: `404 Not Found`
- Example trigger:
  - `ZZ`

Suggested error payload shape:

```json
{
  "error": "STATE_NOT_FOUND",
  "message": "No state exists for abbreviation ZZ"
}
```

## 6. Data model

### 6.1 Primary table

Table: `US_STATES`

| Column | Type | Notes |
| --- | --- | --- |
| `STATE_CODE` | `CHAR(2)` | Primary key, uppercase abbreviation |
| `STATE_NAME` | `VARCHAR2(64)` | Canonical state name |
| `CAPITAL` | `VARCHAR2(64)` | State capital |
| `REGION` | `VARCHAR2(32)` | Region label such as South or Midwest |
| `UPDATED_AT` | `TIMESTAMP` | Defaults to `SYSTIMESTAMP` |

### 6.2 Seed data

- Seed at least the 50 US states.
- The first dataset excludes Washington, DC and contains the 50 US states only.
- Seed data must be deterministic and stored in versioned SQL scripts.

## 7. Application design

### 7.1 Code structure

Planned logical layers:

- `web`: Spring MVC controllers and exception handlers
- `service`: request normalization and orchestration
- `repository`: JDBC queries executed against the pooled `DataSource`
- `model` or `dto`: API response and repository row mapping types
- `config`: Spring and JNDI integration

### 7.2 Build and packaging

- Build tool: Gradle
- Gradle distribution path: committed Gradle Wrapper
- Core plugins: `java`, `war`
- Packaging: `war`
- Java source/target: `1.8`
- Servlet API: provided scope to match Resin runtime

Expected Gradle tasks:

- `./gradlew clean`
- `./gradlew test`
- `./gradlew war`
- `./gradlew check`
- `./gradlew newmanTest` as a custom task that shells out to Newman or invokes a wrapper script

### 7.3 Database access rule

- The application must never construct direct JDBC connections with `DriverManager`.
- The application must depend on a `javax.sql.DataSource` supplied by Resin through JNDI.
- Spring configuration will resolve that `DataSource` with `JndiObjectFactoryBean`, `JndiTemplate`, or equivalent JNDI-based wiring.
- The only database-facing value allowed inside application code is the fixed JNDI lookup name `java:comp/env/jdbc/oracle/states`.

### 7.4 Architecture diagram requirement

- The repository must include a quick-view application-layering diagram that shows the composition of the app.
- Mermaid source is acceptable, but every Mermaid diagram committed to the repo must have an accompanying screenshot asset committed beside it or in the same architecture docs area.
- The first required diagram must show the web, DTO, service, repository port, Oracle repository, domain, Spring configuration, Resin JNDI datasource boundary, and Oracle dependency.
- The initial diagram set lives in `docs/architecture/`.

## 8. Resin connection pooling specification

### 8.1 Ownership model

- Resin owns datasource creation, driver configuration, and connection pooling.
- Application code is limited to borrowing connections from the injected `DataSource`.
- Pool credentials and connection URL are not duplicated in application property files.

### 8.2 Proposed datasource identity

- JNDI name: `jdbc/oracle/states`
- Resin environment: defined at the app or server level depending on whether the datasource should be shared across webapps

### 8.3 Proposed pool settings

Initial pool values for local development:

- Minimum connections: `2`
- Maximum connections: `15`
- Idle timeout: `300s`
- Connection checkout timeout: `15s`
- Validation query or ping: `SELECT 1 FROM DUAL`

These values are starting points and may be tuned during implementation.

### 8.4 Resin configuration expectation

The final Resin configuration must:

- Load the Oracle JDBC driver
- Use `oracle.jdbc.pool.OracleDataSource` as the configured datasource implementation inside Resin's `<database>` resource
- Use the Oracle thin JDBC URL form `jdbc:oracle:thin:@//host:port/service`
- Define a `<database>` resource or equivalent datasource block
- Bind the datasource into JNDI
- Reference credentials and network values from environment variables sourced from `env.dev`

Driver clarification:

- Pool owner: Resin `<database>` resource
- Datasource implementation class: `oracle.jdbc.pool.OracleDataSource`
- JDBC driver artifact supplied to Resin: `com.oracle.database.jdbc:ojdbc8`

### 8.5 Externalization requirement

The project must demonstrate, in both code and runtime configuration, that:

- JDBC URL, database credentials, and pool sizing live outside the WAR in Resin runtime configuration.
- `env.dev` supplies the environment variables consumed by the Resin container startup path.
- Application code does not duplicate connection URL, database password, or pool settings.
- Application code only resolves and uses the pooled JNDI `DataSource`.

### 8.6 Repository coding standard

- Use try-with-resources for `Connection`, `PreparedStatement`, and `ResultSet`
- Close resources on every code path
- Keep SQL centralized and deterministic

## 9. Docker and environment specification

### 9.1 Compose design

Expected top-level files:

- `docker-compose.yml`
- `build.gradle`
- `settings.gradle`
- `gradlew`
- `gradlew.bat`
- `gradle/wrapper/*`
- `Dockerfile.app`
- optional Oracle Docker build assets if not using a prebuilt image

### 9.2 Application container

The application image will:

- Start from an OpenJDK 8 base image
- Install or unpack Resin 4.0.x
- Copy the built WAR into the Resin webapps directory
- Copy Resin configuration files
- Expose the HTTP port used for local testing
- Start cleanly under Docker Compose without requiring manual in-container steps before endpoint testing

### 9.3 Oracle container

- Runtime image: `gvenzl/oracle-free:23-slim`
- Reason: free Oracle runtime with a practical ARM-compatible local Docker path
- The repository does not commit Oracle binaries.

### 9.4 `env.dev`

All generated local credentials and environment-specific values must be saved in `env.dev`.

Required variables are expected to include:

```dotenv
ORACLE_PASSWORD=
APP_DB_USER=
APP_DB_PASSWORD=
ORACLE_HOST=oracle-db
ORACLE_PORT=1521
ORACLE_SERVICE_NAME=FREEPDB1
DB_POOL_MIN=2
DB_POOL_MAX=15
DB_POOL_IDLE_TIME=300s
DB_POOL_WAIT_TIME=15s
```

Rules:

- `env.dev` must be local-only and excluded from version control.
- Recommended file mode: `600`.
- Docker Compose should load it with `--env-file env.dev` or equivalent.
- Resin should consume the values through environment variable substitution.
- The JNDI name remains a fixed application contract and is not treated as a runtime-tunable secret.

### 9.5 Runnable endpoint-test environment

The implementation must provide a local runtime path with these properties:

- `docker compose --env-file env.dev up --build` starts the Oracle and Resin application containers
- the application container becomes reachable on the documented HTTP port
- the state lookup endpoint can be exercised against the running containers with curl, Postman, or Newman
- no manual container mutation is required between startup and endpoint testing

## 10. Testing specification

### 10.1 Unit tests

Framework:

- JUnit 5
- Mockito
- Spring Test where useful

Required unit test coverage:

- Controller returns `200` for a valid abbreviation
- Controller returns `400` for malformed abbreviations
- Service uppercases and validates input
- Service or repository returns not-found behavior for unknown abbreviations
- Repository row mapping is deterministic

### 10.2 Endpoint tests

Tooling:

- Postman collection for manual and automated endpoint verification
- Newman CLI for CI or scripted local execution
- Gradle task integration so CI/CD has one primary entrypoint

Required Postman cases:

- `GET /api/v1/states/TX` returns `200` and expected JSON fields
- `GET /api/v1/states/tx` is normalized and returns `200`
- `GET /api/v1/states/ZZ` returns `404`
- `GET /api/v1/states/TEX` returns `400`

These endpoint tests are expected to run against the live Dockerized stack for integration validation.

### 10.3 CI/CD execution contract

CI/CD must use Gradle Wrapper commands as the standard entrypoint:

- Build and unit test: `./gradlew clean test war`
- Full verification: `./gradlew check`
- API verification hook: `./gradlew newmanTest`

The CI system may still run Docker commands, but Gradle remains the authoritative build lifecycle interface.

## 11. Observability and operability

- Application and Resin logs should be written to stdout/stderr for Docker-friendly collection.
- Database startup and schema initialization logs should be visible through `docker compose logs`.
- A simple health probe endpoint may be added later, but it is not required for the first implementation slice.

## 12. Acceptance criteria

The implementation will be considered complete when all of the following are true:

- The stack starts locally with Docker Compose.
- `docker compose --env-file env.dev up --build` yields a runnable Docker-based endpoint test environment.
- Oracle Free contains the schema and seed data required for state lookup.
- The Resin-hosted application serves `GET /api/v1/states/{abbreviation}`.
- The endpoint returns `200`, `400`, and `404` responses as specified.
- Resin manages the JDBC connection pool and the application uses only the pooled JNDI datasource.
- JDBC URL, credentials, and pool settings are externalized from the application into Resin runtime configuration.
- The repository includes an application-layering architecture diagram plus committed screenshot.
- JUnit tests pass.
- Postman or Newman endpoint tests pass.
- Gradle Wrapper commands required by CI/CD pass.
- Generated credentials are written to `env.dev`.

## 13. Open questions to confirm during implementation

- Should the response payload stay as the fuller record, or be trimmed to only the fields needed by the consumer?
- Does the team want a read-only health endpoint in the first delivery?
