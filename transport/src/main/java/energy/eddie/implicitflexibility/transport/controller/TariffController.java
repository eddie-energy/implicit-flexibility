package energy.eddie.implicitflexibility.transport.controller;

import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import energy.eddie.implicitflexibility.interactions.tariff.TariffInteractionRegistry;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/countries/{countryCode}/tariffs")
public class TariffController {

    private final TariffInteractionRegistry tariffInteractionRegistry;
    private final ObjectMapper objectMapper;

    public TariffController(TariffInteractionRegistry tariffInteractionRegistry,
                            ObjectMapper objectMapper) {
        this.tariffInteractionRegistry = tariffInteractionRegistry;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public RepresentationModel<?> discover(@PathVariable String countryCode) {
        TariffInformationRepository interaction = tariffInteractionRegistry.getTariffs(countryCode);
        Class<?> queryType = interaction.getQueryType();

        Link selfLink = linkTo(methodOn(TariffController.class)
                        .discover(countryCode))
                        .withSelfRel();

        Link queryLink = Affordances.of(linkTo(methodOn(TariffController.class)
                        .query(countryCode, null))
                        .withRel("query")
                ).afford(HttpMethod.POST)
                .withInput(queryType)
                .withName("query")
                .toLink();

        return new RepresentationModel<>().add(selfLink).add(queryLink);
    }

    @PostMapping
    public ResponseEntity<?> query(@PathVariable String countryCode,
                                   @RequestBody JsonNode request) {
        var interaction = tariffInteractionRegistry.getTariffs(countryCode);
        Object query = objectMapper.convertValue(request, interaction.getQueryType());
        return ResponseEntity.ok(interaction.execute(query));
    }
}