package energy.eddie.implicitflexibility.transport.tariff;

import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import tools.jackson.databind.node.JsonNodeFactory;

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
