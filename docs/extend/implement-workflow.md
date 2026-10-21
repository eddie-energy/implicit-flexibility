# Implement Tariff Discovery

Discovery is only implemented when the provider supports a workflow that allows clients to identify other information before retrieving tariffs.

The implementation should:

1. Define the discovery entry point.
2. Expose the operations supported by the provider.
3. Accept provider-specific request models.
4. Return results suitable for the next discovery step.
5. Provide the appropriate HAL links and HAL-FORMS templates.

The discovery sequence depends on the provider.
For example, the E-Control integration uses distinct query types for discovering grid operators, brands, and products (electricity and gas).

```text
GET /countries/XX/tariffs
        |
        v
Provider-specific discovery link
        |
        v
Discovery entry point
        |
        +--> Grid operators
        |
        +--> Brands
        |
        +--> Products
```

The framework uses the `TariffDiscovery` abstraction to expose provider-specific discovery entry points.
In this case, the `ExampleProviderTariffInformationRepository` should implement the `getDiscoveryLink` method, referencing the `ExampleProviderTariffController` class in which the discovery workflow is implemented.

```java

@Component
public class ExampleProviderTariffInformationRepository implements TariffInformationRepository, TariffDiscovery {
    
    ...

    @Override
    public Link getDiscoveryLink(String countryCode) {
        return linkTo(methodOn(ExampleProviderTariffController.class)
                              .discovery())
                .withRel("discovery");
    }
}
```

The discovery controller or service should map the models to the external provider's API through the provider client.
HAL links allow clients to discover available operations.
The provider-specific discovery entry point should expose links to the supported operations. Responses from each operation should contain the links or information necessary for the client to continue the workflow, according to the existing API design.

Given the `ExampleProviderClient` from the [4. Implement the external API client](./add-datasource#_4-implement-the-external-api-client) step in the add data source manual and the `TariffInformationAffordance` in the transport module, the `ExampleProviderTariffController` can be added to implement the above workflow as follows:

```java

@RestController
@RequestMapping("/countries/XX/tariffs")
public class ExampleProviderTariffController {
    private final ExampleProviderClient client;
    private final TariffInformationAffordance tariffInformationAffordance;

    public ExampleProviderTariffController(
            ExampleProviderClient client,
            TariffInformationAffordance tariffInformationAffordance
    ) {
        this.client = client;
        this.tariffInformationAffordance = tariffInformationAffordance;
    }
    
    ...
}
```

HAL-FORMS describes the inputs a client should submit to an operation.
Use the framework's existing affordance and form configuration mechanisms to expose the correct request schema for each discovery operation.
Given the grid operators as a starting point, the following method can be added for the discovery endpoint, which calls the `generateDiscoveryLink` method of the `TariffInformationAffordance`.

```java

@GetMapping("/discovery")
public RepresentationModel<?> discovery() {
    var model = new RepresentationModel<>();
    model.add(linkTo(methodOn(ExampleProviderTariffController.class).discovery()).withSelfRel());
    model.add(tariffInformationAffordance.generateDiscoveryLink("grid-operators",
                                                                this.getClass(),
                                                                GridOperatorQuery.class));
    return model;
}
```

This will call the `grid-operators` endpoint to execute the relevant method in the client and reference the next step in the discovery workflow.

For example:

```java

@PostMapping("/grid-operators")
public CollectionModel<EntityModel<GridOperator>> gridOperators(@RequestBody GridOperatorQuery query) {
    var res = client.findGridOperators(query).stream().map(gridOperator -> {
        EntityModel<GridOperator> model = EntityModel.of(gridOperator);
        model.add(tariffInformationAffordance.generateDiscoveryLink("brands",
                                                                    this.getClass(),
                                                                    BrandSearch.class));
        return model;
    }).toList();

    return CollectionModel.of(res);
}
```

The same applies for the rest until the final step that uses the `create` method of the `TariffInformationAffordance` rather than the `generateDiscoveryLink`.

```java
tariffInformationAffordance.create(ExampleProviderDataSource.COUNTRY_CODE)
```

Finally, relevant query classes can be configured as follows:

```java

@Component
public class ExampleProviderFormTypeProvider implements FormTypeProvider {

    @Override
    public List<Class<?>> getFormTypes() {
        return List.of(
                GridOperatorQuery.class,
                BrandSearch.class,
                ...
        );
    }
}
```