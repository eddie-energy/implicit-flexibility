package energy.eddie.implicitflexibility.transport.tariff;

import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TariffInformationAffordanceTest {

    private TariffProviderRegistry registry;
    private TariffInformationAffordance affordance;

    @BeforeEach
    void setUp() {
        registry = mock(TariffProviderRegistry.class);
        affordance = new TariffInformationAffordance(registry);
    }

    @Test
    void create() {
        // Given
        TariffInformationRepository repository = mock(TariffInformationRepository.class);

        when(registry.getTariffs("AT")).thenReturn(repository);
        doReturn(this.getClass()).when(repository).getQueryType();

        // When
        Link result = affordance.create("AT");

        // Then
        assertEquals("tariff-information", result.getRel().value());
        assertTrue(result.getHref().endsWith("/countries/AT/tariffs"));
        verify(registry).getTariffs("AT");
        verify(repository).getQueryType();
    }
}