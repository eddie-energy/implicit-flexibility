package energy.eddie.implicitflexibility.datasource.at.econtrol.query;

import energy.eddie.datasource.at.econtrol.RateGasProductsRequest;
import energy.eddie.datasource.at.econtrol.RatePowerProductsRequest;
import energy.eddie.datasource.at.econtrol.RateProductsRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductQueryMapperTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductQueryMapper mapper;

    @Test
    void mapPowerProductQuery() {
        // Given
        PowerProductQuery query = mock(PowerProductQuery.class);
        RatePowerProductsRequest request = mock(RatePowerProductsRequest.class);

        when(objectMapper.convertValue(query, RatePowerProductsRequest.class)).thenReturn(request);

        // When
        RateProductsRequest result = mapper.toRateProductsRequest(query);

        // Then
        assertSame(request, result);
        verify(objectMapper).convertValue(query, RatePowerProductsRequest.class);
    }

    @Test
    void mapGasProductQuery() {
        // Given
        GasProductQuery query = mock(GasProductQuery.class);
        RateGasProductsRequest request = mock(RateGasProductsRequest.class);

        when(objectMapper.convertValue(query, RateGasProductsRequest.class)).thenReturn(request);

        // When
        RateProductsRequest result = mapper.toRateProductsRequest(query);

        // Then
        assertSame(request, result);
        verify(objectMapper).convertValue(query, RateGasProductsRequest.class);
    }

    @Test
    void mapProductQuery_NotSupported() {
        // Given
        ProductQuery query = mock(ProductQuery.class);

        // When / Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> mapper.toRateProductsRequest(query));
        assertEquals("Unsupported product query type: " +
                     query.getClass().getName(), exception.getMessage());
        verifyNoInteractions(objectMapper);
    }
}
