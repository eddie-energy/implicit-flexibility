package energy.eddie.implicitflexibility.datasource.at.econtrol;

import energy.eddie.datasource.at.econtrol.ProductContractTerm;
import energy.eddie.datasource.at.econtrol.ProductContractTermInfo;
import energy.eddie.implicitflexibility.datasource.at.econtrol.config.EControlProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EControlClient {

    private final RestClient restClient;

    public EControlClient(EControlProperties properties) {
        this.restClient = RestClient.builder().baseUrl(properties.getBaseUrl().toString())
                .defaultHeaders(headers -> {
                    if (properties.getUsername() != null && !properties.getUsername().isBlank()) {
                        headers.setBasicAuth(properties.getUsername(), properties.getPassword());
                    }
                })
                .build();
    }

    public ProductContractTermInfo getPriceInfo(String productId) {

        return restClient.get()
                .uri("/products/{productId}/price-info", productId)
                .retrieve()
                .body(ProductContractTermInfo.class);
    }

    public ProductContractTerm getContract(String productId) {
        return restClient.get()
                .uri("/products/{productId}/contract", productId)
                .retrieve()
                .body(ProductContractTerm.class);
    }
}
