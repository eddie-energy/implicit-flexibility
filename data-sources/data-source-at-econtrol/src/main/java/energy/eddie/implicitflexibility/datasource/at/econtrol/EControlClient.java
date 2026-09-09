package energy.eddie.implicitflexibility.datasource.at.econtrol;

import energy.eddie.datasource.at.econtrol.*;
import energy.eddie.implicitflexibility.datasource.at.econtrol.config.EControlProperties;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GridOperatorQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQueryMapper;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceNotFoundException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceServerException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Component
public class EControlClient {

    private final RestClient restClient;
    private final ProductQueryMapper productQueryMapper;
    private static final Logger LOG = LoggerFactory.getLogger(EControlClient.class);


    public EControlClient(EControlProperties properties,
                          ProductQueryMapper productQueryMapper) {
        this.restClient = RestClient.builder().baseUrl(properties.baseUrl().toString())
                .defaultHeaders(headers ->
                        headers.setBasicAuth(properties.username(), properties.password()))
                .build();
        this.productQueryMapper = productQueryMapper;
    }

    public List<GridOperator> findGridOperators(Optional<GridOperatorQuery> query) {
        return execute("grid operators", () -> restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/grid-operators");

                    query.ifPresent(q -> {
                        if (q.zipCode() != null) {
                            uriBuilder.queryParam("zipCode", q.zipCode());
                        }
                        if (q.energyType() != null) {
                            uriBuilder.queryParam("energyType", q.energyType());
                        }
                    });

                    return uriBuilder.build();
                }).retrieve()
                .body(new ParameterizedTypeReference<>() {
                }));
    }

    public BrandSearchResult findBrands(Optional<BrandSearch> query) {
        return execute("brands", () -> {
            var requestSpec = restClient.post().uri("/brands/search");
            query.ifPresent(requestSpec::body);
            return requestSpec.retrieve().body(new ParameterizedTypeReference<>() {
            });
        });
    }

    public CurrentProductData findProducts(Optional<ProductQuery> query) {
        ProductQuery q = query.orElseThrow(() ->
                new IllegalArgumentException("Product query parameters must be provided"));
        Map<String, Object> uriVariables = Map.of(
                "brandId", q.getBrandId(),
                "energyType", q.getEnergyType().name().toLowerCase(Locale.ROOT),
                "smartMeter", q.getSmartMeter());
        RateProductsRequest requestPayload = productQueryMapper.toRateProductsRequest(q);

        return execute("products", () -> restClient.post()
                .uri("/brands/{brandId}/products/{energyType}/search?smartMeter={smartMeter}", uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestPayload)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                }));
    }

    public ProductContractTermInfo getPriceInfo(String productId) {
        return execute("price information", () -> restClient.get()
                .uri("/products/{productId}/price-info", productId)
                .retrieve()
                .body(ProductContractTermInfo.class));
    }

    public ProductContractTerm getContract(String productId) {
        return execute("product contract", () -> restClient.get()
                .uri("/products/{productId}/contract", productId)
                .retrieve()
                .body(ProductContractTerm.class));
    }

    private <T> T execute(String operation, Supplier<T> action) {
        try {
            return action.get();
        } catch (HttpClientErrorException.NotFound e) {
            LOG.error("Data not found while retrieving {} from data source {}",
                    operation, EControlDataSource.DATA_SOURCE_ID, e);

            throw new DataSourceNotFoundException(EControlDataSource.DATA_SOURCE_ID,
                    "The requested data was not found.", e);

        } catch (HttpServerErrorException e) {
            LOG.error("Data source server error while retrieving {} from data source {}",
                    operation, EControlDataSource.DATA_SOURCE_ID, e);

            throw new DataSourceServerException(EControlDataSource.DATA_SOURCE_ID,
                    "The requested data could not be retrieved from the data source.", e);

        } catch (ResourceAccessException e) {
            LOG.error("Data source unavailable while retrieving {} from data source {}",
                    operation, EControlDataSource.DATA_SOURCE_ID, e);

            throw new DataSourceUnavailableException(EControlDataSource.DATA_SOURCE_ID,
                    "The data source is currently unavailable.", e);
        }
    }
}
