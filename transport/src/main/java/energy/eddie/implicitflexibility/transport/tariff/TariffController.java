package energy.eddie.implicitflexibility.transport.tariff;

import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/countries/{countryCode}/tariffs")
public class TariffController {

    private final TariffProviderRegistry tariffInteractionRegistry;
    private final ObjectMapper objectMapper;
    private final TariffInformationAffordance tariffInformationAffordance;

    public TariffController(
            TariffProviderRegistry tariffInteractionRegistry,
            ObjectMapper objectMapper,
            TariffInformationAffordance tariffInformationAffordance
    ) {
        this.tariffInteractionRegistry = tariffInteractionRegistry;
        this.objectMapper = objectMapper;
        this.tariffInformationAffordance = tariffInformationAffordance;
    }

    @GetMapping
    public RepresentationModel<?> discover(@PathVariable String countryCode) {
        var model = new RepresentationModel<>();
        var interaction = tariffInteractionRegistry.getTariffs(countryCode);

        model.add(linkTo(methodOn(TariffController.class).discover(countryCode)).withSelfRel());
        model.add(tariffInformationAffordance.create(countryCode));

        if (interaction instanceof TariffDiscovery tariffDiscovery) {
            model.add(tariffDiscovery.getDiscoveryLink(countryCode));
        }

        return model;
    }

    @PostMapping
    public Object query(@PathVariable String countryCode, @RequestBody JsonNode request) {
        var interaction = tariffInteractionRegistry.getTariffs(countryCode);
        var query = objectMapper.convertValue(request, interaction.getQueryType());
        return ResponseEntity.ok(interaction.execute(query));
    }
}