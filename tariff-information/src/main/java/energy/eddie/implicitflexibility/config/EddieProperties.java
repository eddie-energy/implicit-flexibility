package energy.eddie.implicitflexibility.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "eddie")
public class EddieProperties {

    private URI publicUrl;

    public URI getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(URI publicUrl) {
        this.publicUrl = publicUrl;
    }
}
