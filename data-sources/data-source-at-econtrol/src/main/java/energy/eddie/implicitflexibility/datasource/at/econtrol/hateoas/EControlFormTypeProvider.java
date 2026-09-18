package energy.eddie.implicitflexibility.datasource.at.econtrol.hateoas;

import energy.eddie.datasource.at.econtrol.BrandSearch;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.EControlTariffQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GridOperatorQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GasProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.PowerProductQuery;
import energy.eddie.implicitflexibility.interactions.hateoas.FormTypeProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EControlFormTypeProvider implements FormTypeProvider {

    @Override
    public List<Class<?>> getFormTypes() {
        return List.of(
                GridOperatorQuery.class,
                BrandSearch.class,
                GasProductQuery.class,
                PowerProductQuery.class,
                EControlTariffQuery.class
        );
    }
}
