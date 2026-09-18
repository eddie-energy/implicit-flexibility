package energy.eddie.implicitflexibility.datasource.at.econtrol;

import energy.eddie.datasource.at.econtrol.*;
import energy.eddie.implicitflexibility.datasource.at.econtrol.config.EControlProperties;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GasProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GridOperatorQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.PowerProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQueryMapper;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceNotFoundException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(MockitoExtension.class)
class EControlClientTest {

    private static final String BASE_URL = "http://e-control.test";

    private MockRestServiceServer server;
    private EControlClient client;

    @Mock
    private ProductQueryMapper productQueryMapper;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        server = MockRestServiceServer.bindTo(builder).build();
        EControlProperties properties = new EControlProperties(true,
                                                               URI.create(BASE_URL),
                                                               "username",
                                                               "password");
        client = new EControlClient(builder, properties, productQueryMapper);
    }

    @Test
    void testFindGridOperators() {
        // Given
        GridOperatorQuery query = mock(GridOperatorQuery.class);
        doReturn("4040").when(query).zipCode();
        doReturn(EnergyType.POWER).when(query).energyType();

        server.expect(requestTo(BASE_URL + "/grid-operators?zipCode=4040&energyType=POWER"))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withJson("""
                                           [
                                               {
                                                   "id": 14951
                                               }
                                           ]
                                           """));

        // When
        List<GridOperator> result = client.findGridOperators(query);

        // Then
        assertEquals(1, result.size());
        assertEquals(14951, result.getFirst().getId());
        server.verify();
    }

    @Test
    void testFindBrands() {
        // Given
        BrandSearch query = mock(BrandSearch.class);

        server.expect(requestTo(BASE_URL + "/brands/search"))
              .andExpect(method(HttpMethod.POST))
              .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
              .andRespond(withJson("""
                                           {
                                               "brands": []
                                           }
                                           """));

        // When
        BrandSearchResult result = client.findBrands(query);

        // Then
        assertEquals(List.of(), result.getBrandData());
        server.verify();
    }

    @Test
    void testFindPowerProducts() {
        // Given
        PowerProductQuery query = mock(PowerProductQuery.class);
        RatePowerProductsRequest request = mock(RatePowerProductsRequest.class);

        doReturn(123L).when(query).getBrandId();
        doReturn(EnergyType.POWER).when(query).getEnergyType();
        doReturn(false).when(query).getSmartMeter();
        doReturn(request).when(productQueryMapper).toRateProductsRequest(query);

        server.expect(requestTo(BASE_URL + "/brands/123/products/power/search?smartMeter=false"))
              .andExpect(method(HttpMethod.POST))
              .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
              .andRespond(withJson("""
                                           {}
                                           """));

        // When
        CurrentProductData result = client.findProducts(query);

        // Then
        assertNotNull(result);
        verify(productQueryMapper).toRateProductsRequest(query);

        server.verify();
    }

    @Test
    void testFindGasProducts() {
        // Given
        GasProductQuery query = mock(GasProductQuery.class);
        RateGasProductsRequest request = mock(RateGasProductsRequest.class);

        doReturn(456L).when(query).getBrandId();
        doReturn(EnergyType.GAS).when(query).getEnergyType();
        doReturn(true).when(query).getSmartMeter();
        doReturn(request).when(productQueryMapper).toRateProductsRequest(query);

        server.expect(requestTo(BASE_URL + "/brands/456/products/gas/search?smartMeter=true"))
              .andExpect(method(HttpMethod.POST))
              .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
              .andRespond(withJson("""
                                           {}
                                           """));

        // When
        client.findProducts(query);

        // Then
        verify(productQueryMapper).toRateProductsRequest(query);

        server.verify();
    }

    @Test
    void testGetPriceInfo() {
        // Given
        server.expect(requestTo(BASE_URL + "/products/product-123/price-info"))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withJson("""
                                           {}
                                           """));

        // When
        ProductContractTermInfo result = client.getPriceInfo("product-123");

        // Then
        assertNotNull(result);
        server.verify();
    }

    @Test
    void testGetContract() {
        // Given
        server.expect(requestTo(BASE_URL + "/products/product-123/contract"))
              .andExpect(method(HttpMethod.GET))
              .andRespond(withJson("""
                                           {}
                                           """));

        // When
        ProductContractTerm result = client.getContract("product-123");

        // Then
        assertNotNull(result);
        server.verify();
    }

    @Test
    void getPriceInfo_NotFoundException() {
        // Given
        server.expect(requestTo(BASE_URL + "/products/product-123/price-info"))
              .andRespond(withStatus(NOT_FOUND));

        // When
        DataSourceNotFoundException exception = assertThrows(DataSourceNotFoundException.class,
                                                             () -> client.getPriceInfo("product-123"));

        // Then
        assertEquals(EControlDataSource.DATA_SOURCE_ID, exception.getDataSourceId());
        assertEquals("The requested data was not found.", exception.getMessage());

        server.verify();
    }

    @Test
    void getPriceInfo_ServerError() {
        // Given
        server.expect(requestTo(BASE_URL + "/products/product-123/price-info"))
              .andRespond(withServerError());

        // When
        DataSourceServerException exception = assertThrows(DataSourceServerException.class,
                                                           () -> client.getPriceInfo("product-123"));

        // Then
        assertEquals(EControlDataSource.DATA_SOURCE_ID, exception.getDataSourceId());
        assertEquals("The requested data could not be retrieved from the data source.",
                     exception.getMessage());

        server.verify();
    }

    private static DefaultResponseCreator withJson(String json) {
        return org.springframework.test.web.client.response.MockRestResponseCreators
                .withSuccess(json, MediaType.APPLICATION_JSON);
    }
}
