package energy.eddie.implicitflexibility.transport.tariff;

import org.springframework.hateoas.Link;

public interface TariffDiscovery {
    Link getDiscoveryLink(String countryCode);
}
