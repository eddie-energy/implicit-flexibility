package energy.eddie.implicitflexibility.datasource.at.econtrol;

import energy.eddie.datasource.at.econtrol.*;
import energy.eddie.implicitflexibility.datasource.at.econtrol.config.EControlProperties;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GridOperatorQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQueryMapper;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceResponseErrorHandler;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class EControlClient {

    private final RestClient restClient;
    private final ProductQueryMapper productQueryMapper;

    public EControlClient(
            RestClient.Builder restClientBuilder,
            EControlProperties properties,
            ProductQueryMapper productQueryMapper
    ) {
        this.restClient = restClientBuilder.baseUrl(properties.baseUrl().toString())
                                           .defaultHeaders(headers -> headers.setBasicAuth(
                                                   properties.username(), properties.password()))
                                           .defaultStatusHandler(
                                                   new DataSourceResponseErrorHandler(EControlDataSource.DATA_SOURCE_ID))
                                           .build();
        this.productQueryMapper = productQueryMapper;
    }

    public List<GridOperator> findGridOperators(GridOperatorQuery query) {
        return restClient.get()
                         .uri(uriBuilder -> uriBuilder
                                 .path("/grid-operators")
                                 .queryParam("zipCode",
                                             query.zipCode())
                                 .queryParam("energyType",
                                             query.energyType())
                                 .build())
                         .retrieve()
                         .requiredBody(new ParameterizedTypeReference<>() {
                         });
    }

    public BrandSearchResult findBrands(BrandSearch query) {
        return restClient.post()
                         .uri("/brands/search")
                         .body(query)
                         .retrieve()
                         .requiredBody(new ParameterizedTypeReference<>() {
                         });
    }

    public CurrentProductData findProducts(ProductQuery query) {
        Map<String, Object> uriVariables = Map.of(
                "brandId", query.getBrandId(),
                "energyType", query.getEnergyType().name().toLowerCase(Locale.ROOT),
                "smartMeter", query.getSmartMeter());
        RateProductsRequest requestPayload = productQueryMapper.toRateProductsRequest(query);

        return restClient.post()
                         .uri("/brands/{brandId}/products/{energyType}/search?smartMeter={smartMeter}",
                              uriVariables)
                         .contentType(MediaType.APPLICATION_JSON)
                         .body(requestPayload)
                         .retrieve()
                         .requiredBody(new ParameterizedTypeReference<>() {
                         });
    }

    public ProductContractTermInfo getPriceInfo(String productId) {
        return restClient.get()
                         .uri("/products/{productId}/price-info", productId)
                         .retrieve()
                         .requiredBody(ProductContractTermInfo.class);
    }

    public ProductContractTerm getContract(String productId) {
        return restClient.get()
                         .uri("/products/{productId}/contract", productId)
                         .retrieve()
                         .requiredBody(ProductContractTerm.class);
    }
}
