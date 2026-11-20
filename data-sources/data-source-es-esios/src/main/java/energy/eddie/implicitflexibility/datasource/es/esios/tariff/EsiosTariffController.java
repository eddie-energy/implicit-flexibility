package energy.eddie.implicitflexibility.datasource.es.esios.tariff;

import energy.eddie.implicitflexibility.datasource.es.esios.EsiosClient;
import energy.eddie.implicitflexibility.datasource.es.esios.EsiosDataSource;
import energy.eddie.implicitflexibility.datasource.es.esios.models.IndicatorList;
import energy.eddie.implicitflexibility.transport.tariff.TariffInformationAffordance;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/countries/ES/tariffs")
public class EsiosTariffController {

    private final EsiosClient client;
    private final TariffInformationAffordance tariffInformationAffordance;


    public EsiosTariffController(EsiosClient client, TariffInformationAffordance tariffInformationAffordance) {
        this.client = client;
        this.tariffInformationAffordance = tariffInformationAffordance;
    }

    @GetMapping("/indicators")
    public EntityModel<IndicatorList> indicators() {
        var res = client.getIndicators();
        EntityModel<IndicatorList> model = EntityModel.of(res);
        model.add(tariffInformationAffordance.create(EsiosDataSource.COUNTRY_CODE));
        return model;
    }

    @GetMapping("/discovery")
    public RepresentationModel<?> discovery() {
        var model = new RepresentationModel<>();
        model.add(linkTo(methodOn(EsiosTariffController.class).discovery()).withSelfRel());
        model.add(tariffInformationAffordance.generateDiscoveryLink("indicators",
                                                                    this.getClass(),
                                                                    null));
        return model;
    }
}
