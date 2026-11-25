package energy.eddie.implicitflexibility.datasource.es.esios.tariff;

import energy.eddie.implicitflexibility.datasource.es.esios.EsiosClient;
import energy.eddie.implicitflexibility.datasource.es.esios.EsiosDataSource;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosTariffQuery;
import energy.eddie.implicitflexibility.interactions.tariff.TariffQueryExecutor;
import energy.eddie.implicitflexibility.transport.tariff.TariffDiscovery;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EsiosTariffQueryExecutor implements TariffQueryExecutor, TariffDiscovery {

    private final EsiosClient client;
    private final Validator validator;

    public EsiosTariffQueryExecutor(EsiosClient client, Validator validator) {
        this.client = client;
        this.validator = validator;
    }

    @Override
    public String getCountryCode() {
        return EsiosDataSource.COUNTRY_CODE;
    }

    @Override
    public Class<?> getQueryType() {
        return EsiosTariffQuery.class;
    }

    @Override
    public Object execute(Object query) {
        EsiosTariffQuery esiosQuery = (EsiosTariffQuery) query;
        Set<ConstraintViolation<Object>> violations = validator.validate(esiosQuery);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return client.getIndicatorTariff(esiosQuery);
    }

    @Override
    public Link getDiscoveryLink() {
        return linkTo(methodOn(EsiosTariffController.class)
                              .discovery())
                .withRel("discovery");
    }
}
