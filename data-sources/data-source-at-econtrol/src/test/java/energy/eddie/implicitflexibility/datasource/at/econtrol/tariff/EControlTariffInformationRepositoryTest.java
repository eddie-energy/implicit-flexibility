package energy.eddie.implicitflexibility.datasource.at.econtrol.tariff;

import energy.eddie.datasource.at.econtrol.ProductContractTerm;
import energy.eddie.datasource.at.econtrol.ProductContractTermInfo;
import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlClient;
import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlDataSource;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.EControlInformationType;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.EControlTariffQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class EControlTariffInformationRepositoryTest {

    private EControlClient client;
    private EControlTariffInformationRepository repository;

    @BeforeEach
    void setUp() {
        client = mock(EControlClient.class);
        repository = new EControlTariffInformationRepository(client);
    }

    @Test
    void testGetCountryCode() {
        // Given / When
        String result = repository.getCountryCode();

        // Then
        assertEquals(EControlDataSource.COUNTRY_CODE, result);
    }

    @Test
    void testGetQueryType() {
        // Given / When
        Class<?> result = repository.getQueryType();

        // Then
        assertEquals(EControlTariffQuery.class, result);
    }

    @Test
    void testExecuteContractQuery() {
        // Given
        String productId = "product-123";
        EControlTariffQuery query = mock(EControlTariffQuery.class);
        ProductContractTerm expectedResult = mock(ProductContractTerm.class);

        when(query.informationType()).thenReturn(EControlInformationType.CONTRACT);
        when(query.productId()).thenReturn(productId);
        when(client.getContract(productId)).thenReturn(expectedResult);

        // When
        Object result = repository.execute(query);

        // Then
        assertSame(expectedResult, result);
        verify(client).getContract(productId);
    }

    @Test
    void testExecutePriceInfoQuery() {
        // Given
        String productId = "product-123";
        EControlTariffQuery query = mock(EControlTariffQuery.class);
        ProductContractTermInfo expectedResult = mock(ProductContractTermInfo.class);

        when(query.informationType()).thenReturn(EControlInformationType.PRICE_INFO);
        when(query.productId()).thenReturn(productId);
        when(client.getPriceInfo(productId)).thenReturn(expectedResult);

        // When
        Object result = repository.execute(query);

        // Then
        assertSame(expectedResult, result);
        verify(client).getPriceInfo(productId);
    }

    @Test
    void testGetDiscoveryLink() {
        // Given
        String countryCode = "AT";

        // When
        Link result = repository.getDiscoveryLink(countryCode);

        // Then
        assertEquals("discovery", result.getRel().value());
        assertEquals("/countries/AT/tariffs/discovery", result.getHref());
    }
}
