package energy.eddie.implicitflexibility.interactions.tariff;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TariffProviderRegistryTest {

    @Test
    void registerTariffProviders() {
        // Given
        TariffQueryExecutor atProvider = tariffProvider("AT");
        TariffQueryExecutor dkProvider = tariffProvider("DK");

        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider, dkProvider));
        Collection<TariffQueryExecutor> result = registry.getAll("AT");

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(atProvider));
        assertFalse(result.contains(dkProvider));
    }

    @Test
    void registerTariffProviders_NotRegisteredCountry() {
        // Given
        TariffQueryExecutor atProvider = tariffProvider("AT");

        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));
        Collection<TariffQueryExecutor> result = registry.getAll("DK");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void registerTariffProviders_DuplicateCountryCodes() {
        // Given
        TariffQueryExecutor first = tariffProvider("AT");
        TariffQueryExecutor second = tariffProvider("AT");
        List<TariffQueryExecutor> providers = List.of(first, second);

        // When / Then
        assertThrows(IllegalStateException.class, () -> new TariffProviderRegistry(providers));
    }

    @Test
    void registerTariffProviders_NoTariffProvidersRegistered() {
        // Given
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of());

        // When
        Collection<TariffQueryExecutor> result = registry.getAll("AT");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findTariffProviderForCountry() {
        // Given
        TariffQueryExecutor atProvider = tariffProvider("AT");

        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));
        Optional<TariffQueryExecutor> result = registry.find("AT");

        // Then
        assertTrue(result.isPresent());
        assertSame(atProvider, result.get());
    }

    @Test
    void findTariffProvider_NotAvailable() {
        // Given
        TariffQueryExecutor atProvider = tariffProvider("AT");


        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));
        Optional<TariffQueryExecutor> result = registry.find("DK");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getTariffsForCountry() {
        // Given
        TariffQueryExecutor atProvider = tariffProvider("AT");


        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));
        TariffQueryExecutor result = registry.getTariffs("AT");

        // Then
        assertSame(atProvider, result);
    }

    @Test
    void getTariffs_TariffProviderNotAvailable() {
        // Given
        TariffQueryExecutor atProvider = tariffProvider("AT");

        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                          () -> registry.getTariffs("DK"));
        assertEquals("Tariff interaction not available for DK", exception.getMessage());
    }

    private TariffQueryExecutor tariffProvider(String countryCode) {
        TariffQueryExecutor provider = mock(TariffQueryExecutor.class);
        when(provider.getCountryCode()).thenReturn(countryCode);
        return provider;
    }
}
