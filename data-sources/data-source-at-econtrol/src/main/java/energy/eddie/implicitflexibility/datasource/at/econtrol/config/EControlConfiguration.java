package energy.eddie.implicitflexibility.datasource.at.econtrol.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(EControlProperties.class)
public class EControlConfiguration {

    @Bean
    RestClient eControlRestClient(EControlProperties properties) {
        return RestClient.builder()
                         .baseUrl(properties.baseUrl().toString())
                         .defaultHeaders(headers -> headers.setBasicAuth(
                                 properties.username(), properties.password()))
                         .build();
    }
}
