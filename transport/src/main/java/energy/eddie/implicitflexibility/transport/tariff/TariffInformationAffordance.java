package energy.eddie.implicitflexibility.transport.tariff;

import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TariffInformationAffordance {
    private final TariffProviderRegistry registry;

    public TariffInformationAffordance(TariffProviderRegistry registry) {
        this.registry = registry;
    }

    public Link create(String countryCode) {
        TariffInformationRepository repository = registry.getTariffs(countryCode);

        return Affordances.of(linkTo(methodOn(TariffController.class)
                                             .query(countryCode, Optional.empty()))
                                      .withRel("tariff-information"))
                          .afford(HttpMethod.POST)
                          .withInput(repository.getQueryType())
                          .withName("query")
                          .toLink();
    }
}
