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
        TariffInformationRepository atProvider = tariffProvider("AT");
        TariffInformationRepository dkProvider = tariffProvider("DK");

        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider, dkProvider));
        Collection<TariffInformationRepository> result = registry.getAll("AT");

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(atProvider));
        assertFalse(result.contains(dkProvider));
    }

    @Test
    void registerTariffProviders_NotRegisteredCountry() {
        // Given
        TariffInformationRepository atProvider = tariffProvider("AT");

        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));
        Collection<TariffInformationRepository> result = registry.getAll("DK");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void registerTariffProviders_DuplicateCountryCodes() {
        // Given
        TariffInformationRepository first = tariffProvider("AT");
        TariffInformationRepository second = tariffProvider("AT");
        List<TariffInformationRepository> providers = List.of(first, second);

        // When / Then
        assertThrows(IllegalStateException.class, () -> new TariffProviderRegistry(providers));
    }

    @Test
    void registerTariffProviders_NoTariffProvidersRegistered() {
        // Given
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of());

        // When
        Collection<TariffInformationRepository> result = registry.getAll("AT");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findTariffProviderForCountry() {
        // Given
        TariffInformationRepository atProvider = tariffProvider("AT");

        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));
        Optional<TariffInformationRepository> result = registry.find("AT");

        // Then
        assertTrue(result.isPresent());
        assertSame(atProvider, result.get());
    }

    @Test
    void findTariffProvider_NotAvailable() {
        // Given
        TariffInformationRepository atProvider = tariffProvider("AT");


        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));
        Optional<TariffInformationRepository> result = registry.find("DK");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getTariffsForCountry() {
        // Given
        TariffInformationRepository atProvider = tariffProvider("AT");


        // When
        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));
        TariffInformationRepository result = registry.getTariffs("AT");

        // Then
        assertSame(atProvider, result);
    }

    @Test
    void getTariffs_TariffProviderNotAvailable() {
        // Given
        TariffInformationRepository atProvider = tariffProvider("AT");

        TariffProviderRegistry registry = new TariffProviderRegistry(List.of(atProvider));

        // When / Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                                                          () -> registry.getTariffs("DK"));
        assertEquals("Tariff interaction not available for DK", exception.getMessage());
    }

    private TariffInformationRepository tariffProvider(String countryCode) {
        TariffInformationRepository provider = mock(TariffInformationRepository.class);
        when(provider.getCountryCode()).thenReturn(countryCode);
        return provider;
    }
}
