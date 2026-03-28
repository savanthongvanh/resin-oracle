Now execute the project deliberately from the specification and `plans.md`.

Non-negotiable constraint

- Do not stop after each milestone to ask for confirmation when the scope is already clear.
- Treat `plans.md` as the source of truth for what gets built next.
- The current repository state is specification-first; broad implementation begins only after the specification is coherent.

Execution rules (follow strictly)

- Read `prompt.md`, `plans.md`, `implement.md`, `documentation.md`, and `specification.md` before resuming work in a later session.
- Make the smallest reviewable change that advances the active milestone.
- Use the parallel-stream model in `plans.md` whenever dependencies allow it; do not serialize work that can safely proceed in parallel.
- After every milestone:
  - run the milestone verification commands
  - fix failures immediately
  - update tests for the milestone's core behavior
  - update `plans.md` and `documentation.md` to match reality
- If a bug is discovered:
  - write a failing test first when practical
  - fix the bug
  - confirm the test passes
  - record the decision or fix note in `plans.md`
- When handling database access:
  - use only the Resin-managed JNDI `DataSource`
  - do not use `DriverManager.getConnection` in application code
  - keep JDBC URL, credentials, and pool limits out of application code so the app demonstrates externalized app-server-managed pooling
  - close borrowed connections, statements, and result sets with try-with-resources
- When handling secrets:
  - write generated local credentials into `env.dev`
  - keep `env.dev` out of version control
  - prefer `chmod 600 env.dev` for local protection
- When handling build and CI/CD work:
  - use the Gradle Wrapper as the standard entrypoint
  - keep build, test, packaging, and Newman automation reachable through documented Gradle tasks
  - avoid introducing Maven-based pipeline dependencies
- When handling documentation:
  - keep architecture diagrams current when the application composition changes
  - if Mermaid is used, keep an accompanying screenshot committed in the repo

Completion criteria

- The specification remains consistent with the shipped implementation.
- All planned milestones that matter for delivery are complete or explicitly descoped with rationale.
- The validation checklist in `plans.md` passes.
- `documentation.md` is accurate for operators.
- The shipped result includes a runnable Docker Compose stack that can be started locally to test the REST endpoints.
- The shipped result demonstrates that database pool configuration is externalized from the application into Resin runtime configuration.
- The shipped result includes an application-layering architecture diagram plus screenshot.
- The shipped result matches the deliverable in `prompt.md`.
