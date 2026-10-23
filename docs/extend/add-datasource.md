# Add Data Source

## Overview

This guide explains how to integrate a new energy tariff information provider.

Each provider can expose tariff information through its own API, authentication mechanism, request format, and product discovery process.

To add new data sources, implement the required interfaces, providing the appropriate configuration and registering the relevant Spring components in the following steps:

## 1. Create the data-source module

For example, if a new provider is called ExampleProvider for country with code XX, the structure will be `data-sources/data-source-xx-example/`.

## 2. Implement the data-source abstraction

The implementation should provide the required Id and country information as below:

```java

@Component
@ConditionalOnProperty(prefix = "data-source.xx.example-provider", name = "enabled", havingValue = "true")
public class ExampleDataSource implements DataSource {

    @Override
    public String getId() {
        return "example-provider";
    }

    @Override
    public String getCountry() {
        return "XX";
    }
}
```

## 3. Add configuration properties

Each integration should define its own configuration-properties class. This keeps provider-specific settings separate and gives the integration a single, typed entry point for its configuration.

Use a unique configuration prefix for each provider.

Assume the following properties (url, username and password) for the running example in addition to the default `enabled`:

```properties
data-source.xx.example-provider.enabled=true
data-source.xx.example-provider.base-url=https://example-provider.com
data-source.xx.example-provider.username=${EXAMPLE_PROVIDER_USERNAME}
data-source.xx.example-provider.password=${EXAMPLE_PROVIDER_PASSWORD}
```

Use Spring Boot's `@ConfigurationProperties` to bind the external configuration to a Java record or class.

For example:

```java

@ConfigurationProperties(prefix = "data-source.xx.example-provider")
public record ExampleProviderProperties(
        boolean enabled,
        URI baseUrl,
        String username,
        String password
) {}
```

For configuration that requires additional validation, use Jakarta Bean Validation annotations such as `@NotNull`, `@NotBlank`, or `@URL` where appropriate and supported by the project's dependencies.

The main application already uses `@ConfigurationPropertiesScan` to discover configuration-properties classes in the scanned packages.

For example, the following property:

```properties
data-source.xx.example-provider.enabled=true
```

binds to the `enabled` field of `ExampleProviderProperties`.

## 4. Implement the external API client

The external API client belongs in the provider's integration module. Its responsibility is to communicate with the external service and translate between the provider's API and the framework's application-level code.

The project uses Spring's `RestClient` for synchronous HTTP communication.
Inject a `RestClient.Builder` into the client and build a configured `RestClient` instance for that provider.

```java

@Component
public class ExampleProviderClient {

    private final RestClient restClient;

    public ExampleProviderClient(
            ExampleProviderProperties properties,
            RestClient.Builder builder
    ) {
        this.restClient = builder
                .baseUrl(properties.baseUrl().toString())
                .build();
    }
}
```

This example shows the basic structure. Add authentication, headers, error handling, and other required configuration before using the client.
Implement the appropriate authentication flow for providers that use basic authentication (username and password), bearer tokens, API keys, OAuth 2.0, or another authentication mechanism.

Then, use the request body support provided by `RestClient` to submit typed request objects.
Keep provider-specific request models in the provider module.

For example:

```java
public ExampleResponse getInformation(
        ExampleRequest request
) {
    return restClient.post()
                     .uri("/information")
                     .contentType(MediaType.APPLICATION_JSON)
                     .body(request)
                     .retrieve()
                     .body(ExampleResponse.class);
}
```

`RestClient.retrieve()` supports status handling through its response-status handling mechanisms. Use these to translate provider-specific failures into meaningful exceptions or application-level errors.
Keep the handling consistent with the project's existing error-handling conventions.

## 5. Implement tariff information retrieval

To make a provider available through the generic tariff API, implement the `TariffInformationRepository` abstraction.
The repository connects the framework's generic tariff request mechanism to the provider-specific query and client.

A provider-specific implementation should:

1. Return the country code supported by the integration.
2. Return the provider-specific query class.
3. Execute the supplied query.
4. Invoke the external API through the provider client.
5. Return the resulting tariff information.

For example:

```java

@Component
public class ExampleTariffInformationRepository
        implements TariffInformationRepository {

    private final ExampleProviderClient client;

    public ExampleTariffInformationRepository(
            ExampleProviderClient client
    ) {
        this.client = client;
    }

    @Override
    public String getCountryCode() {
        return "XX";
    }

    @Override
    public Class<?> getQueryType() {
        return ExampleTariffQuery.class;
    }

    @Override
    public Object execute(Object query) {
        ExampleTariffQuery tariffQuery =
                (ExampleTariffQuery) query;

        return client.getTariffInformation(tariffQuery);
    }
}
```

The `getQueryType()` method tells the framework which Java type represents the request body for this repository.
The generic tariff controller uses this type to convert the incoming JSON request into the correct provider-specific query object.

The `execute(Object query)` method receives the typed query object after the framework converts the request JSON.

Other providers can use different query fields, operations, and response models.

## 6. Implement tariff discovery (Optional)

The framework's tariff retrieval mechanism is separate from the optional discovery capability.
A provider can implement `TariffInformationRepository` without implementing `TariffDiscovery`.

Check the [next page](./implement-workflow) for more details about tariff discovery workflow.

## 7. Register the implementation

For a new provider, place Spring components within the packages scanned by the application, or explicitly configure scanning if the integration is outside those packages.

Typical provider components include:

- Configuration-properties classes.
- The external API client.
- The tariff information repository.
- The provider-specific discovery controller.
- Any provider-specific services or mappers.

`DataSourceRegistry` registers the available `DataSource` implementations.
In the current framework, it collects the data-source beans and indexes them by country code and reports enabled data sources.
A new implementation of `DataSource` makes the provider available to this registry when Spring creates the bean.
The data-source registry represents the availability of general data sources; it does not replace the tariff information registry.

`TariffProviderRegistry` registers `TariffInformationRepository` implementations.
It makes tariff information operations available to the generic tariff controller and resolves repositories by country code.
For a provider to handle tariff queries, its repository must be registered as a Spring bean and discovered by the registry.
The registry is responsible for routing a country-specific tariff request to the appropriate repository.

## 8. Add request models and HAL-FORMS support

The framework uses request models to define provider-specific operations and HAL-FORMS to describe the requests that clients can submit.

Create a dedicated request type that reflect the provider's fields required by the operation.
Enums help express the supported choices in Java and can be exposed as selectable options in HAL-FORMS.

The framework uses `FormTypeProvider` implementations to register request types that need special HAL-FORMS treatment, including enum options.
Follow the existing implementation pattern when adding new provider-specific query types.
For example, the E-Control integration provides its query types through `EControlFormTypeProvider` in the `hateoas` package.

The framework's HAL-FORMS configuration uses `HalFormsEnumOptions.register` to register enum values.
When a new request model introduces an enum that should appear as selectable options, ensure that the enum is registered through the existing configuration mechanism.
The registration should expose the enum's supported values to clients so that they can construct valid requests.
