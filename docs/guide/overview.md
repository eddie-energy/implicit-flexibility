# Overview

The EDDIE Implicit Flexibility framework provides a common interface for accessing tariff information from energy providers.

Different providers expose their data through different APIs, authentication mechanisms, request formats, and product discovery processes. The framework supports provider-specific integrations and exposes a consistent API to clients.

The framework is designed to support the integration of additional data sources without requiring the shared API to be redesigned for every provider.

## Goals

Clients can request tariff information through a shared endpoint. The request model can depend on the selected country because providers may require different parameters.

Some providers require clients to perform several operations before a tariff can be retrieved. For example, a client might need to select a grid operator, identify a brand, and choose a product.
The framework can expose such provider-specific operations through discovery endpoints and navigable links.
The exact discovery workflow depends on the provider.

A new integration can implement the framework's integration interfaces and provide the configuration, API client, request models, and response handling required by the external provider.

The framework aims to:

- **Provide a common API:** Expose tariff information through shared REST endpoints.
- **Support multiple data sources:** Allow country-specific integrations to connect to different energy providers.
- **Encapsulate provider-specific logic:** Keep external API communication, request models, and response mapping within the corresponding integration.
- **Support tariff discovery:** Allow clients to discover the information required to retrieve tariff data.
- **Enable extensibility:** Add new integrations through defined interfaces.

## Technology Stack

The framework is built using the following technologies:

- Java 21
- Spring Boot
- Gradle
- Spring Web
- Spring HATEOAS
- HAL-FORMS

## Application Startup

1. The tariff-information module starts the Spring Boot application.
2. Spring discovers configuration properties and components.
3. Enabled data-source implementations are instantiated according to their configuration.
4. Registries collect the available data sources and tariff information repositories.
5. The transport layer exposes the endpoints backed by the registered implementations.