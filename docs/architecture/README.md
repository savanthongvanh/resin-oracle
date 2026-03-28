# Architecture Diagrams

This directory holds architecture diagrams that give a quick view of the application's composition.

Requirements for this repository:

- Mermaid source is acceptable, but every committed Mermaid diagram must have an accompanying screenshot asset.
- The first required diagram is the application layering view for the Resin-hosted service.
- The README uses at least two diagrams so the layering view and the runtime-ownership view are both visible.

## Application layering

- Mermaid source: [application-layering.mmd](/Users/savanthongvanh/workspaces/resin-oracle/docs/architecture/application-layering.mmd)
- Screenshot: [application-layering.png](/Users/savanthongvanh/workspaces/resin-oracle/docs/architecture/application-layering.png)

![Application layering screenshot](./application-layering.png)

The layering diagram shows:

- The WAR composition across web, DTO, service, repository port, Oracle repository, domain, and Spring config.
- The boundary between application code and Resin-managed runtime concerns.
- The externalized datasource and pool configuration path from `env.dev` into Resin JNDI.
- The exact datasource implementation class `oracle.jdbc.pool.OracleDataSource`.
- The Oracle JDBC runtime artifact path through `ojdbc8`.
- The Oracle Free dependency used by the repository layer through the Resin-managed `DataSource`.

## Connection ownership flow

- Mermaid source: [connection-ownership-flow.mmd](/Users/savanthongvanh/workspaces/resin-oracle/docs/architecture/connection-ownership-flow.mmd)
- Screenshot: [connection-ownership-flow.png](/Users/savanthongvanh/workspaces/resin-oracle/docs/architecture/connection-ownership-flow.png)

![Connection ownership flow screenshot](./connection-ownership-flow.png)

This diagram shows:

- Where runtime values come from.
- That Resin, not the application, creates the pooled datasource.
- That `ojdbc8` supplies the Oracle JDBC implementation.
- That application code only consumes the JNDI datasource.

## Regeneration

Render or refresh all architecture screenshots with:

```bash
./scripts/render-architecture-diagrams.sh
```
