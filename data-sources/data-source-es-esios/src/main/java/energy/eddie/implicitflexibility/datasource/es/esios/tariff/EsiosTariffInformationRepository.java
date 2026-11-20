package energy.eddie.implicitflexibility.datasource.es.esios.tariff;

import energy.eddie.implicitflexibility.datasource.es.esios.EsiosClient;
import energy.eddie.implicitflexibility.datasource.es.esios.EsiosDataSource;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosTariffQuery;
import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EsiosTariffInformationRepository implements TariffInformationRepository {

    private final EsiosClient client;
    private final Validator validator;

    public EsiosTariffInformationRepository(EsiosClient client, Validator validator) {
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
}
