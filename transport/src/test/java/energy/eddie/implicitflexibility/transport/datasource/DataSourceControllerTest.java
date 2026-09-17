package energy.eddie.implicitflexibility.transport.datasource;

import energy.eddie.implicitflexibility.interactions.datasource.DataSource;
import energy.eddie.implicitflexibility.interactions.datasource.DataSourceRegistry;
import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.IanaLinkRelations;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataSourceControllerTest {

    private DataSourceRegistry dataSourceRegistry;
    private TariffProviderRegistry tariffProviderRegistry;
    private DataSourceController controller;

    @BeforeEach
    void setUp() {
        dataSourceRegistry = mock(DataSourceRegistry.class);
        tariffProviderRegistry = mock(TariffProviderRegistry.class);
        controller = new DataSourceController(dataSourceRegistry, tariffProviderRegistry);
    }

    @Test
    void getDataSources() {
        // Given
        DataSource atEControl = dataSource("at.e-control", "AT");
        DataSource atOther = dataSource("at.other", "AT");
        DataSource dkOther = dataSource("dk.other", "DK");
        when(dataSourceRegistry.getAll()).thenReturn(List.of(atEControl, atOther, dkOther));

        // When
        Collection<DataSourceRepresentation> result = controller.getDataSources();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(representation ->
                                                    hasSelfLink(representation, "/countries/AT")));
        assertTrue(result.stream().anyMatch(representation ->
                                                    hasSelfLink(representation, "/countries/DK")));
    }

    @Test
    void getDataSources_Empty() {
        // Given
        when(dataSourceRegistry.getAll()).thenReturn(List.of());

        // When
        Collection<DataSourceRepresentation> result = controller.getDataSources();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDataSourceByCountry() {
        // Given
        when(tariffProviderRegistry.getAll("AT")).thenReturn(List.of());

        // When
        DataSourceRepresentation result = controller.getDataSource("AT");

        // Then
        assertTrue(hasSelfLink(result, "/countries/AT"));
    }

    @Test
    void createTariffsLink() {
        // Given
        TariffInformationRepository provider = tariffProvider("AT");

        when(tariffProviderRegistry.getAll("AT")).thenReturn(List.of(provider));

        // When
        DataSourceRepresentation result = controller.getDataSource("AT");

        // Then
        assertTrue(result.getLink("tariffs").isPresent());
        assertEquals("/countries/AT/tariffs",
                     result.getLink("tariffs").orElseThrow().getHref());
    }

    @Test
    void createTariffsLink_MultipleProviders() {
        // Given
        TariffInformationRepository firstProvider = tariffProvider("AT");
        TariffInformationRepository secondProvider = tariffProvider("AT");
        when(tariffProviderRegistry.getAll("AT")).thenReturn(List.of(firstProvider, secondProvider));

        // When
        DataSourceRepresentation result = controller.getDataSource("AT");

        // Then
        assertEquals(1, result.getLinks("tariffs").size());
    }

    @Test
    void createTariffsLink_NoProvider() {
        // Given
        when(tariffProviderRegistry.getAll("AT")).thenReturn(List.of());

        // When
        DataSourceRepresentation result = controller.getDataSource("AT");

        // Then
        assertTrue(result.getLink("tariffs").isEmpty());
    }

    private boolean hasSelfLink(DataSourceRepresentation representation, String expectedHref) {
        return representation
                .getLink(IanaLinkRelations.SELF)
                .map(link -> link.getHref().equals(expectedHref))
                .orElse(false);
    }

    private DataSource dataSource(String id, String country) {
        DataSource dataSource = mock(DataSource.class);

        when(dataSource.getId()).thenReturn(id);
        when(dataSource.getCountry()).thenReturn(country);

        return dataSource;
    }

    private TariffInformationRepository tariffProvider(String countryCode) {
        TariffInformationRepository provider = mock(TariffInformationRepository.class);
        when(provider.getCountryCode()).thenReturn(countryCode);
        return provider;
    }
}
