package energy.eddie.implicitflexibility.transport.controller;

import energy.eddie.implicitflexibility.interactions.datasource.DataSourceRegistry;
import energy.eddie.implicitflexibility.interactions.tariff.TariffInteractionRegistry;
import energy.eddie.implicitflexibility.transport.representation.DataSourceRepresentation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/countries")
public class DataSourceController {

    private final DataSourceRegistry dataSourceRegistry;
    private final TariffInteractionRegistry tariffInteractionRegistry;

    public DataSourceController(DataSourceRegistry dataSourceRegistry,
                                TariffInteractionRegistry tariffInteractionRegistry) {
        this.dataSourceRegistry = dataSourceRegistry;
        this.tariffInteractionRegistry = tariffInteractionRegistry;
    }

    @GetMapping
    public Collection<DataSourceRepresentation> getDataSources() {
        return dataSourceRegistry.getAll()
                .stream()
                .map(dataSource -> {
                    DataSourceRepresentation representation = new DataSourceRepresentation("countries");
                    String countryCode = dataSource.getCountry();
                    representation.add(
                            linkTo(methodOn(DataSourceController.class).getDataSource(countryCode))
                                    .withRel(countryCode)
                    );
                    return representation;
                }).toList();
    }

    @GetMapping("/{countryCode}")
    public DataSourceRepresentation getDataSource(@PathVariable String countryCode) {
        DataSourceRepresentation representation = new DataSourceRepresentation(countryCode);
        representation.add(linkTo(methodOn(DataSourceController.class).getDataSource(countryCode)).withSelfRel());
        tariffInteractionRegistry.getAll(countryCode).forEach(interaction -> representation.add(
                linkTo(methodOn(TariffController.class).discover(countryCode)).withRel("tariffs")
        ));

        return representation;
    }
}