package energy.eddie.implicitflexibility.transport.datasource;

import energy.eddie.implicitflexibility.interactions.datasource.DataSource;
import energy.eddie.implicitflexibility.interactions.datasource.DataSourceRegistry;
import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import energy.eddie.implicitflexibility.transport.tariff.TariffController;
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
    private final TariffProviderRegistry tariffProviderRegistry;

    public DataSourceController(
            DataSourceRegistry dataSourceRegistry,
            TariffProviderRegistry tariffProviderRegistry
    ) {
        this.dataSourceRegistry = dataSourceRegistry;
        this.tariffProviderRegistry = tariffProviderRegistry;
    }

    @GetMapping
    public Collection<DataSourceRepresentation> getDataSources() {
        return dataSourceRegistry.getAll()
                                 .stream()
                                 .map(DataSource::getCountry)
                                 .distinct()
                                 .map(this::createCountryRepresentation)
                                 .toList();
    }

    @GetMapping("/{countryCode}")
    public DataSourceRepresentation getDataSource(@PathVariable String countryCode) {
        DataSourceRepresentation representation = createCountryRepresentation(countryCode);

        if (!tariffProviderRegistry.getAll(countryCode).isEmpty()) {
            representation.add(linkTo(methodOn(TariffController.class).discover(countryCode)).withRel("tariffs"));
        }

        return representation;
    }

    private DataSourceRepresentation createCountryRepresentation(String countryCode) {
        DataSourceRepresentation representation = new DataSourceRepresentation(countryCode);
        representation.add(linkTo(methodOn(DataSourceController.class).getDataSource(countryCode)).withSelfRel());
        return representation;
    }
}