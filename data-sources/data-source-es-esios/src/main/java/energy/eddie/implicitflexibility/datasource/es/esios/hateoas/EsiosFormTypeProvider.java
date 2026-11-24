package energy.eddie.implicitflexibility.datasource.es.esios.hateoas;

import energy.eddie.implicitflexibility.datasource.es.esios.models.Indicator;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosTariffQuery;
import energy.eddie.implicitflexibility.interactions.hateoas.FormTypeProvider;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class EsiosFormTypeProvider implements FormTypeProvider {

    @Override
    public Collection<Class<?>> getFormTypes() {
        return List.of(Indicator.class,
                       EsiosTariffQuery.class);
    }
}
