package energy.eddie.implicitflexibility.datasource.at.econtrol.query;

import energy.eddie.datasource.at.econtrol.RateGasProductsRequest;
import energy.eddie.datasource.at.econtrol.RatePowerProductsRequest;
import energy.eddie.datasource.at.econtrol.RateProductsRequest;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
public class ProductQueryMapper {

    private final ObjectMapper objectMapper;

    public ProductQueryMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RateProductsRequest toRateProductsRequest(ProductQuery query) {
        return switch (query) {
            case PowerProductQuery power -> objectMapper.convertValue(power, RatePowerProductsRequest.class);
            case GasProductQuery gas -> objectMapper.convertValue(gas, RateGasProductsRequest.class);
            default -> throw new IllegalArgumentException("Unsupported product query type: "
                                                          + query.getClass().getName());
        };
    }
}
