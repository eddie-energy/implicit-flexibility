# Architecture

The EDDIE Implicit Flexibility Framework provides a modular structure for
discovering and retrieving tariff information from different data sources.

The framework follows the Hexagonal Architecture to separate core interactions, transport concerns and external data-source integrations.

## Modules

### Interactions

Defines the application-level interactions and configuration metadata used by the framework to retrieve tariff information and perform discovery operations.

![UML interactions_module](../images/UML_interactions_module.png)

- `DataSource` represents an integration with an external data source.
  It provides identifying information, including the data source identifier and its associated country.
  A data source can also have its own configuration and API client.

- `DataSourceRegistry` collects the enabled `data source implementations and indexes them by country.

- `TariffInformationRepository` defines the country-specific query type and tariff information execution operation.
  Provider-specific implementations translate the framework's incoming request into the external API operations required to retrieve the result.
  They should include the country code supported, the request type expected by the provider and the operation that executes a tariff information query.

- `TariffProviderRegistry` collects tariff information repository implementations and resolves them by country code.
  The shared tariff API uses this registry to locate the appropriate repository for an incoming request.

### Transport

Exposes the framework functionality through HTTP endpoints and represents resources using Spring HATEOAS and HAL-FORMS.

![UML transport_module](../images/UML_transport_module.png)

- `TariffDiscovery` represents the discovery entry point provided by an integration.
  It allows the framework to expose links to provider-specific discovery operations.
  Discovery operations can be used to identify the information required for a subsequent tariff query.

- `TariffInformationAffordance` creates the HAL-FORMS affordance for tariff information requests.

### Data Sources

Contains integrations with external tariff-information providers.

Each data source implements the relevant interfaces and can provide country-specific query types and discovery workflows.
The request body can differ by country even though the endpoint remains the same

### Tariff Information Application

Provides the main Spring Boot application that assembles the framework components.

## Tariff Retrieval

The request flow is as follows:

1. A client sends a request to the common tariff API, which is received by the transport controller.
2. The country associated with the request is identified.
3. The tariff provider registry resolves the corresponding `TariffInformationRepository`.
4. The request body is converted into the repository's query type.
5. The query is sent to the repository.
6. The tariff information is requested.
7. The client retrieves the tariff information from the external API.
8. The response from the external API is received by the client
9. The tariff information result is received by the corresponding data source's repository
10. The result is forwarded to the transport module.
11. The HTTP response is returned to the client.

The following diagram illustrates how a tariff information request is processed by the framework.

![DFD implicit flexibility](../images/DFD_implicit_flexibility.png)