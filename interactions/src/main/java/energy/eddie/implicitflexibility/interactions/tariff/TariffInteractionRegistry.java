package energy.eddie.implicitflexibility.interactions.tariff;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TariffInteractionRegistry {

    private final Map<String, TariffInformationRepository> interactions;

    public TariffInteractionRegistry(List<TariffInformationRepository> interactions) {
        this.interactions = interactions.stream().collect(
                Collectors.toUnmodifiableMap(TariffInformationRepository::getCountryCode, Function.identity())
        );
    }

    public Collection<TariffInformationRepository> getAll(String countryCode) {
        return interactions.values()
                .stream()
                .filter(interaction -> interaction.getCountryCode()
                        .equals(countryCode))
                .toList();
    }

    public Optional<TariffInformationRepository> find(String countryCode) {
        return Optional.ofNullable(interactions.get(countryCode));
    }

    public TariffInformationRepository getTariffs(String countryCode) {
        return find(countryCode).orElseThrow(() ->
                new IllegalArgumentException("Tariff interaction not available for " + countryCode)
        );
    }
}
