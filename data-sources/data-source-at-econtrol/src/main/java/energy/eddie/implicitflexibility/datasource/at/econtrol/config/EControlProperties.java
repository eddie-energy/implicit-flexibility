package energy.eddie.implicitflexibility.datasource.at.econtrol.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "data-source.at.e-control")
public record EControlProperties (@NotNull boolean enabled,
                                  @NotNull URI baseUrl,
                                  @NotBlank String username,
                                  @NotNull String password) { }
