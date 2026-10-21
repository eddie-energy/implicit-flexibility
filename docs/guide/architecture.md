# Architecture

The EDDIE Implicit Flexibility Framework provides a modular structure for
discovering and retrieving tariff information from different data sources.

The framework follows a hexagonal architecture to separate core interactions, transport concerns and external data-source integrations.

## Modules

### Interactions

Defines the application-level interactions and configuration metadata used by the framework to retrieve tariff information and perform discovery operations.

The `DataSource` abstraction represents an integration with an external
data source.
It provides identifying information, including the data source identifier
and its associated country.
A data source can also have its own configuration and API client.

```java
public interface DataSource {
    String getId();

    String getCountry();
}
```

The `DataSourceRegistry` collects the enabled `data source implementations
and indexes them by country.

```java

@Component
public class DataSourceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceRegistry.class);
    private final Map<String, DataSource> enabledDataSources;

    public DataSourceRegistry(List<DataSource> dataSources) {
        this.enabledDataSources = dataSources.stream()
                                             .collect(Collectors.toUnmodifiableMap(
                                                     DataSource::getId,
                                                     Function.identity()
                                             ));

        if (dataSources.isEmpty()) {
            LOG.warn("No data sources are enabled.");
        } else {
            dataSources.stream()
                       .map(DataSource::getId)
                       .sorted()
                       .forEach(id -> LOG.info("Data source enabled: {}", id));
        }
    }

    public Collection<DataSource> getAll() {
        return enabledDataSources.values();
    }

    public Collection<DataSource> getByCountry(String country) {
        return enabledDataSources.values().stream()
                                 .filter(dataSource -> dataSource.getCountry().equals(country))
                                 .toList();
    }
}
```

`TariffInformationRepository` defines the country-specific query type and tariff information execution operation.
Provider-specific implementations translate the framework's incoming request into the external API operations required to retrieve the result.
They should included the country code supported, the request type expected by the provider and the operation that executes a tariff information query.

```java
public interface TariffInformationRepository {
    String getCountryCode();

    Class<?> getQueryType();

    Object execute(Object query);
}
```

The `TariffProviderRegistry` collects tariff information repository implementations and resolves them by country code.
The shared tariff API uses this registry to locate the appropriate repository for an incoming request.

```java

@Component
public class TariffProviderRegistry {

    private final Map<String, TariffInformationRepository> interactions;

    public TariffProviderRegistry(List<TariffInformationRepository> interactions) {
        this.interactions = interactions.stream().collect(
                Collectors.toUnmodifiableMap(TariffInformationRepository::getCountryCode, Function.identity())
        );
    }

    public Collection<TariffInformationRepository> getAll(String countryCode) {
        return interactions.values()
                           .stream()
                           .filter(interaction -> interaction.getCountryCode()
                                                             .equals(countryCode))
                           .toList();
    }

    public Optional<TariffInformationRepository> find(String countryCode) {
        return Optional.ofNullable(interactions.get(countryCode));
    }

    public TariffInformationRepository getTariffs(String countryCode) {
        return find(countryCode).orElseThrow(() ->
                                                     new IllegalArgumentException("Tariff interaction not available for " + countryCode)
        );
    }
}
```

### Transport

Exposes the framework functionality through HTTP endpoints and represents resources using Spring HATEOAS and HAL-FORMS.

The `TariffDiscovery` abstraction represents the discovery entry point provided by an integration.
It allows the framework to expose links to provider-specific discovery operations.
Discovery operations can be used to identify the information required for a subsequent tariff query.

```java
public interface TariffDiscovery {
    Link getDiscoveryLink(String countryCode);
}
```

The `TariffInformationAffordance` creates the HAL-FORMS affordance for tariff information requests.

```java

@Component
public class TariffInformationAffordance {
    private final TariffProviderRegistry registry;

    public TariffInformationAffordance(TariffProviderRegistry registry) {
        this.registry = registry;
    }

    public Link create(String countryCode) {
        TariffInformationRepository repository = registry.getTariffs(countryCode);
        return Affordances.of(linkTo(methodOn(TariffController.class)
                                             .query(countryCode, JsonNodeFactory.instance.objectNode()))
                                      .withRel("tariff-information"))
                          .afford(HttpMethod.POST)
                          .withInput(repository.getQueryType())
                          .withName("query")
                          .toLink();
    }

    public Link generateDiscoveryLink(String name, Class<?> link, Class<?> input) {
        return Affordances.of(linkTo(link).slash(name).withRel(name))
                          .afford(HttpMethod.POST)
                          .withInput(input)
                          .withName(name)
                          .toLink();
    }
}
```

### Data Sources

Contains integrations with external tariff-information providers.

Each data source implements the relevant interfaces and can provide country-specific query types and discovery workflows.
The request body can differ by country even though the endpoint remains the same

### Tariff Information Application

Provides the main Spring Boot application that assembles the framework components.

## Tariff Retrieval

The request flow is as follows:

1. A client sends a request to the common tariff API, which is received by the transport controller.
2. The API identifies the country associated with the request.
3. The tariff provider registry resolves the corresponding `TariffInformationRepository`.
4. The request body is converted into the repository's query type.
5. The repository executes the query.
6. The result is returned to the client through the common API.