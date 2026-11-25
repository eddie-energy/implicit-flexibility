package energy.eddie.implicitflexibility.transport.tariff;

import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import energy.eddie.implicitflexibility.interactions.tariff.TariffQueryExecutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffInformationAffordanceTest {

    @Mock
    private TariffProviderRegistry registry;

    @InjectMocks
    private TariffInformationAffordance affordance;

    @Test
    void create() {
        // Given
        TariffQueryExecutor repository = mock(TariffQueryExecutor.class);

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