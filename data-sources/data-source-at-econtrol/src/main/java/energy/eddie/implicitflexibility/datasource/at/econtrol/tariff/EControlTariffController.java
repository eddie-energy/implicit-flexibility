package energy.eddie.implicitflexibility.datasource.at.econtrol.tariff;

import energy.eddie.datasource.at.econtrol.*;
import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlClient;
import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlDataSource;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GasProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GridOperatorQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.PowerProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQuery;
import energy.eddie.implicitflexibility.transport.tariff.TariffInformationAffordance;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/countries/AT/tariffs")
public class EControlTariffController {
    private final EControlClient client;
    private final TariffInformationAffordance tariffInformationAffordance;

    public EControlTariffController(
            EControlClient client,
            TariffInformationAffordance tariffInformationAffordance
    ) {
        this.client = client;
        this.tariffInformationAffordance = tariffInformationAffordance;
    }

    @PostMapping("/grid-operators")
    public CollectionModel<EntityModel<GridOperator>> gridOperators(@RequestBody Optional<GridOperatorQuery> query) {
        var res = client.findGridOperators(query).stream().map(gridOperator -> {
            EntityModel<GridOperator> model = EntityModel.of(gridOperator);
            Link brandsLink = Affordances.of(linkTo(methodOn(EControlTariffController.class)
                                                            .brands(Optional.empty())).withRel("brands"))
                                         .afford(HttpMethod.POST)
                                         .withInput(BrandSearch.class)
                                         .withName("brands")
                                         .toLink();
            model.add(brandsLink);
            return model;
        }).toList();

        return CollectionModel.of(res);
    }

    @PostMapping("/brands")
    public EntityModel<BrandSearchResult> brands(@RequestBody Optional<BrandSearch> query) {
        var res = client.findBrands(query);
        EntityModel<BrandSearchResult> model = EntityModel.of(res);

        var affordanceBuilder = Affordances.of(linkTo(methodOn(EControlTariffController.class)
                                                              .products(Optional.empty()))
                                                       .withRel("products"))
                                           .afford(HttpMethod.POST);

        query.map(BrandSearch::getEnergyType).ifPresent(energyType -> {
            Class<?> inputType = (energyType == EnergyType.POWER)
                    ? PowerProductQuery.class
                    : GasProductQuery.class;

            affordanceBuilder.withInput(inputType);
        });

        Link productsLink = affordanceBuilder.withName("products").toLink();
        model.add(productsLink);
        return model;
    }

    @PostMapping("/products")
    public EntityModel<CurrentProductData> products(@RequestBody Optional<ProductQuery> query) {
        var res = client.findProducts(query);
        EntityModel<CurrentProductData> model = EntityModel.of(res);
        model.add(tariffInformationAffordance.create(EControlDataSource.COUNTRY_CODE));
        return model;
    }

    @GetMapping("/discovery")
    public RepresentationModel<?> discovery() {
        var model = new RepresentationModel<>();
        model.add(linkTo(methodOn(EControlTariffController.class).discovery())
                          .withSelfRel());
        model.add(linkTo(methodOn(EControlTariffController.class).gridOperators(Optional.empty()))
                          .withRel("grid-operators"));

        return model;
    }
}
