# Resin Oracle Service

You are Codex acting as a senior DevOps engineer and senior Java software engineer. Produce an implementation-ready specification for this project before any broad coding begins.

Core goals

- Define a containerized solution built around OpenJDK 8, the Resin application server, an Oracle free-edition database runtime, and Spring Framework libraries.
- Specify a RESTful endpoint that accepts a US state abbreviation and returns a deterministic JSON response.
- Make the end-state implementation produce a runnable Docker Compose stack that can be started locally to test the REST endpoints.
- Make connection pooling an explicit platform responsibility of Resin, not an application-managed concern.
- Make the delivered system clearly demonstrate that database pool configuration is externalized from the application and owned by app-server runtime configuration.
- Require architecture diagrams that give a quick visual view of the system composition, with Mermaid allowed but accompanied by committed screenshots.
- Define the testing strategy around Postman for endpoint tests and JUnit for unit tests.
- Define how generated credentials will be stored in `env.dev`.

Hard requirements

- Runtime stack must use OpenJDK 8.
- Application server must be Resin.
- Database must use the Oracle free edition. For the shipped local stack, prefer an ARM-compatible Oracle Free image when Oracle XE is not runnable on the host platform.
- Spring Framework should be preferred for application dependencies.
- Gradle, via the Gradle Wrapper, must be the build and CI/CD entrypoint.
- The application must expose a RESTful endpoint that accepts a state abbreviation.
- One of the delivered build outcomes must be a runnable Docker Compose application stack suitable for endpoint testing.
- Connection pooling must be configured in the app server.
- The application must fetch database connections only from the app-server-managed pool.
- The repository must include architectural diagrams for the implementation. Mermaid is acceptable, but each Mermaid diagram must have an accompanying screenshot committed to the repo.
- Any generated credentials must be written to `env.dev`.
- Endpoint authentication is explicitly out of scope for the first version.

Deliverable

- Draft a detailed written specification in this repository that an engineer can implement without re-interpreting the requirements, including an explicit requirement that the shipped build yields a runnable Docker-based endpoint test environment.

Project spec

- The solution will be designed as a two-container local stack orchestrated with Docker Compose: one container for Oracle Free and one for the Resin-hosted Java application.
- The Java application will be packaged as a WAR and deployed into Resin 4.0.x running on OpenJDK 8.
- The project will use Gradle with the `war` plugin and the Gradle Wrapper as the standard local and CI/CD execution path.
- The application code will use Spring MVC for the REST API and Spring JDBC for persistence concerns while resolving its `DataSource` through JNDI from Resin.
- The primary endpoint will be `GET /api/v1/states/{abbreviation}` and will return a canonical state record from Oracle.
- A successful implementation must allow an engineer to run `docker compose --env-file env.dev up --build` and test the endpoint against the running containers.
- Oracle credentials, schema credentials, and environment-specific runtime values will be sourced from `env.dev`, which must be treated as a local secret file and excluded from version control.
- The demonstration must make it obvious that JDBC URL, credentials, and pool limits live in Resin runtime configuration rather than Java application code or property files.
- The documentation set must include an application-layering diagram that quickly shows the composition of the app.
- The specification must call out implementation milestones, acceptance criteria, risks, verification steps, and parallel workstreams for the later build-out.

Process requirements (follow strictly)

1. PLANNING FIRST:
   - Keep `plans.md` authoritative.
   - Make the implementation milestones concrete enough for another engineer to resume from them.
2. SPECIFICATION SECOND:
   - Write the system specification before any broad implementation.
   - Record assumptions and unresolved questions explicitly instead of leaving them implicit.
3. DOCUMENTATION THROUGHOUT:
   - Keep `documentation.md` aligned with the current repo state.
   - Keep the control-plane files usable for a later implementation phase.

Start now.
First, make `plans.md` coherent and project-specific. Do NOT start broad coding until `plans.md` exists and is coherent.
