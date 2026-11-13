package energy.eddie.implicitflexibility.datasource.es.esios.tariff;

import energy.eddie.implicitflexibility.datasource.es.esios.EsiosClient;
import energy.eddie.implicitflexibility.datasource.es.esios.EsiosDataSource;
import energy.eddie.implicitflexibility.datasource.es.esios.models.Indicator;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosTariffQuery;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EsiosTariffInformationRepositoryTest {

    @Mock
    private EsiosClient client;

    @Mock
    private Validator validator;

    @InjectMocks
    private EsiosTariffInformationRepository repository;

    @Test
    void testGetCountryCode() {
        // Given / When
        String result = repository.getCountryCode();

        // Then
        assertEquals(EsiosDataSource.COUNTRY_CODE, result);
    }

    @Test
    void testGetQueryType() {
        // Given / When
        Class<?> result = repository.getQueryType();

        // Then
        assertEquals(EsiosTariffQuery.class, result);
    }

    @Test
    void testExecuteQuery() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery(600, null, null);
        Indicator expectedResult = mock(Indicator.class);

        when(client.getIndicatorTariff(query)).thenReturn(expectedResult);
        when(validator.validate(query)).thenReturn(Set.of());

        // When
        Object result = repository.execute(query);

        // Then
        assertSame(expectedResult, result);
        verify(client).getIndicatorTariff(query);
    }
}
