package energy.eddie.implicitflexibility.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "eddie")
public record EddieProperties (URI publicUrl) { }
