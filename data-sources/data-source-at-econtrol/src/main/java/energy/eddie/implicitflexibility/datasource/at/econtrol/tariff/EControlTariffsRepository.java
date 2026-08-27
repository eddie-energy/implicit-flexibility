package energy.eddie.implicitflexibility.datasource.at.econtrol.tariff;

import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlClient;
import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlDataSource;
import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class EControlTariffsRepository implements TariffInformationRepository {

    private final EControlClient client;
    private static final Logger log = LoggerFactory.getLogger(EControlTariffsRepository.class);

    public EControlTariffsRepository(EControlClient client) {
        this.client = client;
    }

    @Override
    public String getCountryCode() {
        return EControlDataSource.COUNTRY_CODE;
    }

    @Override
    public Class<?> getQueryType() {
        return EControlTariffQuery.class;
    }

    @Override
    public Object execute(Object query) {

        EControlTariffQuery eControlQuery = (EControlTariffQuery) query;
        var informationType = eControlQuery.getInformationType();

        try {
            return switch (informationType) {
                case CONTRACT -> client.getContract(eControlQuery.getProductId());
                case PRICE_INFO -> client.getPriceInfo(eControlQuery.getProductId());
            };
        } catch (HttpServerErrorException e) {
            log.error("Failed fetching {} from E-Control: {}",
                    informationType,
                    e.getResponseBodyAsString()
            );
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "E-Control API returned a server error", e);
        }
    }
}
