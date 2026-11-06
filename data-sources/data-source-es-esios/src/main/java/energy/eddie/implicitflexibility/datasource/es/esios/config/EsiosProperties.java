package energy.eddie.implicitflexibility.datasource.es.esios.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "data-source.es.esios")
public record EsiosProperties(boolean enabled,
                              URI baseUrl,
                              @NotBlank String token) {
}
