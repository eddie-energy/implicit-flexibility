package energy.eddie.implicitflexibility.transport.tariff;

import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.JsonNodeFactory;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/countries/{countryCode}/tariffs")
public class TariffController {

    private final TariffProviderRegistry tariffInteractionRegistry;
    private final ObjectMapper objectMapper;

    public TariffController(
            TariffProviderRegistry tariffInteractionRegistry,
            ObjectMapper objectMapper
    ) {
        this.tariffInteractionRegistry = tariffInteractionRegistry;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public RepresentationModel<?> discover(@PathVariable String countryCode) {
        var model = new RepresentationModel<>();
        var interaction = tariffInteractionRegistry.getTariffs(countryCode);
        var queryType = interaction.getQueryType();

        model.add(linkTo(methodOn(TariffController.class)
                                 .discover(countryCode))
                          .withSelfRel());

        model.add(Affordances.of(linkTo(methodOn(TariffController.class)
                                                .query(countryCode, Optional.empty()))
                                         .withRel("query"))
                             .afford(HttpMethod.POST)
                             .withInput(queryType)
                             .withName("query")
                             .toLink());

        if (interaction instanceof TariffDiscovery tariffDiscovery) {
            model.add(tariffDiscovery.getDiscoveryLink(countryCode));
        }

        return model;
    }

    @PostMapping
    public Object query(@PathVariable String countryCode, @RequestBody Optional<JsonNode> request) {
        var interaction = tariffInteractionRegistry.getTariffs(countryCode);
        JsonNode jsonNode = request.orElseGet(JsonNodeFactory.instance::objectNode);
        var query = objectMapper.convertValue(jsonNode, interaction.getQueryType());
        return ResponseEntity.ok(interaction.execute(query));
    }
}