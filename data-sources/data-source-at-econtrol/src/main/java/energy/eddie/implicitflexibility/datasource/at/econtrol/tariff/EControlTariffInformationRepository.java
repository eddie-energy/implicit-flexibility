package energy.eddie.implicitflexibility.datasource.at.econtrol.tariff;

import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlClient;
import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlDataSource;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.EControlTariffQuery;
import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import energy.eddie.implicitflexibility.transport.tariff.TariffDiscovery;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EControlTariffInformationRepository implements TariffInformationRepository, TariffDiscovery {

    private final EControlClient client;

    public EControlTariffInformationRepository(EControlClient client) {
        this.client = client;
    }

    @Override
    public String getCountryCode() {
        return EControlDataSource.COUNTRY_CODE;
    }

    @Override
    public Class<?> getQueryType() {
        return EControlTariffQuery.class;
    }

    @Override
    public Object execute(Object query) {
        EControlTariffQuery eControlQuery = (EControlTariffQuery) query;
        return switch (eControlQuery.informationType()) {
            case CONTRACT -> client.getContract(eControlQuery.productId());
            case PRICE_INFO -> client.getPriceInfo(eControlQuery.productId());
        };
    }

    @Override
    public Link getDiscoveryLink(String countryCode) {
        return linkTo(methodOn(EControlTariffController.class)
                              .discovery())
                .withRel("discovery");
    }
}
