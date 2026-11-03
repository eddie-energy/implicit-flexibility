# Technical Architecture

The EDDIE Implicit Flexibility Framework supports multiple tariff information providers while maintaining a separation between the transport layer, application interactions, and provider-specific implementations.

## Architectural Decisions

The framework is designed based on the following decisions:

- **Separate modules with explicit functionality influenced by the Hexagonal Architecture**

The framework separates application functionality into the following modules:

| Module               | Responsibility                                                                       |
|----------------------|--------------------------------------------------------------------------------------|
| `tariff-information` | Application entry point and application assembly                                     |
| `transport`          | Exposes the HTTP API and handles incoming requests                                   |
| `interactions`       | Defines application-facing contracts and coordinates tariff information interactions |
| `data-sources`       | Implements the integration with tariff information providers                         |

This structure limits coupling between core functionality and provider-specific implementations for easier maintenance and extension.

- **Resolve implementations through registries**

The framework uses registries to locate implementations based on country or other relevant identifiers. Examples are `TariffProviderRegistry` and `DataSourceRegistry`.

This decision centralizes provider resolution and supports modular extension.

- **Data sources can be enabled or disabled through application configuration**

This allows deployments to select which integrations are active and keeps provider configuration isolated.

- **Use Spring HATEOAS and HAL-FORMS to support API navigation and discovery workflows**

The API can expose links and affordances that guide clients through the available interactions.
The tariff discovery workflow enables clients to progressively discover the required inputs (e.g., grid operators, indicators, etc.) before requesting tariff information.

This makes API capabilities discoverable and supports structured interaction forms.

## Technology Stack

The framework uses the following technologies:

| Technology     | Purpose                                        |
|----------------|------------------------------------------------|
| Java           | Primary programming language                   |
| Spring Boot    | Application framework and dependency injection |
| Spring Web     | HTTP API implementation                        |
| Spring HATEOAS | Hypermedia support                             |
| HAL-FORMS      | Hypermedia forms and affordances               |
| Gradle         | Build automation and dependency management     |
| Jackson        | JSON serialization and deserialization         |
| Mockito        | Mocking in unit tests                          |
| VitePress      | Project documentation                          |

## Configuration

The application uses Spring Boot configuration mechanisms to manage application and data source settings.

Configuration may include:

- Data source enablement.
- Provider base URLs.
- Authentication credentials.
- Application server settings.

## Application Startup

1. The tariff-information module starts the Spring Boot application.
2. Spring discovers configuration properties and components.
3. Enabled data-source implementations are instantiated according to their configuration.
4. Registries collect the available data sources and tariff information repositories.
5. The transport layer exposes the endpoints backed by the registered implementations.

Once startup has completed successfully, the HTTP API is available through the configured server port.
The application exposes country-specific tariff information endpoints and the supported discovery operations.

Refer to the [API documentation](../integrate/api) for endpoint details.