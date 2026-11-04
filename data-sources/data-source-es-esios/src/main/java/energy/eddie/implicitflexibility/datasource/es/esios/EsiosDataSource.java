package energy.eddie.implicitflexibility.datasource.es.esios;

import energy.eddie.implicitflexibility.interactions.datasource.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "data-source.es.esios", name = "enabled", havingValue = "true")
public class EsiosDataSource implements DataSource {

    private static final String DATA_SOURCE_ID = "es.esios";
    private static final String COUNTRY_CODE = "ES";

    @Override
    public String getId() {
        return DATA_SOURCE_ID;
    }

    @Override
    public String getCountry() {
        return COUNTRY_CODE;
    }
}
