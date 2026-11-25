package energy.eddie.implicitflexibility.interactions.tariff;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TariffProviderRegistry {

    private final Map<String, TariffQueryExecutor> interactions;

    public TariffProviderRegistry(List<TariffQueryExecutor> interactions) {
        this.interactions = interactions.stream().collect(
                Collectors.toUnmodifiableMap(TariffQueryExecutor::getCountryCode, Function.identity())
        );
    }

    public Collection<TariffQueryExecutor> getAll(String countryCode) {
        return interactions.values()
                           .stream()
                           .filter(interaction -> interaction.getCountryCode()
                                                             .equals(countryCode))
                           .toList();
    }

    public Optional<TariffQueryExecutor> find(String countryCode) {
        return Optional.ofNullable(interactions.get(countryCode));
    }

    public TariffQueryExecutor getTariffs(String countryCode) {
        return find(countryCode).orElseThrow(() ->
                                                     new IllegalArgumentException(
                                                             "Tariff interaction not available for " + countryCode)
        );
    }
}
