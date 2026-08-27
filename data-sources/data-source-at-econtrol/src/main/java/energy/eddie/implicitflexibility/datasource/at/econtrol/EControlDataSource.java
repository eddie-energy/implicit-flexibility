package energy.eddie.implicitflexibility.datasource.at.econtrol;

import energy.eddie.implicitflexibility.interactions.datasource.DataSource;
import org.springframework.stereotype.Component;

@Component
public class EControlDataSource implements DataSource {

    private static final String DATA_SOURCE_ID = "at.e-control";
    public static final String COUNTRY_CODE = "AT";

    @Override
    public String getId() {
        return DATA_SOURCE_ID;
    }

    @Override
    public String getCountry() {
        return COUNTRY_CODE;
    }
}
