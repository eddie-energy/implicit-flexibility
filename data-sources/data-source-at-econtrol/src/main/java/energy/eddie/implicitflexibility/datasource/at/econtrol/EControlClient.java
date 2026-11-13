package energy.eddie.implicitflexibility.datasource.at.econtrol;

import energy.eddie.datasource.at.econtrol.*;
import energy.eddie.implicitflexibility.datasource.at.econtrol.config.EControlProperties;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GridOperatorQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQueryMapper;
import energy.eddie.implicitflexibility.transport.exception.ClientExceptionHandler;
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
    private final ClientExceptionHandler clientExceptionHandler;

    public EControlClient(
            RestClient.Builder restClientBuilder,
            EControlProperties properties,
            ProductQueryMapper productQueryMapper,
            ClientExceptionHandler clientExceptionHandler
    ) {
        this.restClient = restClientBuilder.baseUrl(properties.baseUrl().toString())
                                           .defaultHeaders(headers -> headers.setBasicAuth(
                                                   properties.username(), properties.password())).build();
        this.productQueryMapper = productQueryMapper;
        this.clientExceptionHandler = clientExceptionHandler;
    }

    public List<GridOperator> findGridOperators(GridOperatorQuery query) {
        return clientExceptionHandler.execute("grid operators",
                                              () -> restClient.get()
                                                              .uri(uriBuilder -> uriBuilder
                                                                      .path("/grid-operators")
                                                                      .queryParam("zipCode",
                                                                                  query.zipCode())
                                                                      .queryParam("energyType",
                                                                                  query.energyType())
                                                                      .build())
                                                              .retrieve()
                                                              .body(new ParameterizedTypeReference<>() {
                                                              }),
                                              EControlDataSource.DATA_SOURCE_ID);
    }

    public BrandSearchResult findBrands(BrandSearch query) {
        return clientExceptionHandler.execute("brands",
                                              () -> restClient.post()
                                                              .uri("/brands/search")
                                                              .body(query)
                                                              .retrieve()
                                                              .body(new ParameterizedTypeReference<>() {
                                                              }),
                                              EControlDataSource.DATA_SOURCE_ID);
    }

    public CurrentProductData findProducts(ProductQuery query) {
        Map<String, Object> uriVariables = Map.of(
                "brandId", query.getBrandId(),
                "energyType", query.getEnergyType().name().toLowerCase(Locale.ROOT),
                "smartMeter", query.getSmartMeter());
        RateProductsRequest requestPayload = productQueryMapper.toRateProductsRequest(query);

        return clientExceptionHandler.execute("products",
                                              () -> restClient.post()
                                                              .uri("/brands/{brandId}/products/{energyType}/search?smartMeter={smartMeter}",
                                                                   uriVariables)
                                                              .contentType(MediaType.APPLICATION_JSON)
                                                              .body(requestPayload)
                                                              .retrieve()
                                                              .body(new ParameterizedTypeReference<>() {
                                                              }),
                                              EControlDataSource.DATA_SOURCE_ID);
    }

    public ProductContractTermInfo getPriceInfo(String productId) {
        return clientExceptionHandler.execute("price information",
                                              () -> restClient.get()
                                                              .uri("/products/{productId}/price-info", productId)
                                                              .retrieve()
                                                              .body(ProductContractTermInfo.class),
                                              EControlDataSource.DATA_SOURCE_ID);
    }

    public ProductContractTerm getContract(String productId) {
        return clientExceptionHandler.execute("product contract",
                                              () -> restClient.get()
                                                              .uri("/products/{productId}/contract", productId)
                                                              .retrieve()
                                                              .body(ProductContractTerm.class),
                                              EControlDataSource.DATA_SOURCE_ID);
    }
}
