# Tariff Information Service

The EDDIE Implicit Flexibility Framework provides a common API to retrieve tariff information and support multiple data sources with country-specific implementations.

If you are new to the framework, start with the [Overview](../guide/overview) and read the [Architecture](../guide/architecture) guide before exploring the API or implementing a new data source.

## Extending the Framework

This section explains how to implement new integrations and provider-specific tariff discovery workflows.

- [Add Data Source](./add-datasource)
  Implement a new data source and connect it to the framework.
- [Implement Tariff Discovery](./implement-workflow)
  Implement a discovery workflow for providers that require multiple steps
  to identify tariff products.