package energy.eddie.implicitflexibility.datasource.at.econtrol.query;

import energy.eddie.datasource.at.econtrol.RateGasProductsRequest;
import energy.eddie.datasource.at.econtrol.RatePowerProductsRequest;
import energy.eddie.datasource.at.econtrol.RateProductsRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductQueryMapperTest {

    private ObjectMapper objectMapper;
    private ProductQueryMapper mapper;

    @BeforeEach
    void setUp() {
        objectMapper = mock(ObjectMapper.class);
        mapper = new ProductQueryMapper(objectMapper);
    }

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
