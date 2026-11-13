package energy.eddie.implicitflexibility.datasource.es.esios;

import energy.eddie.implicitflexibility.datasource.es.esios.config.EsiosProperties;
import energy.eddie.implicitflexibility.datasource.es.esios.models.*;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosTariffQuery;
import energy.eddie.implicitflexibility.transport.exception.ClientExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class EsiosClientTest {
    private static final String BASE_URL = "https://api.esios.ree.es";
    private static final String TOKEN = "test-token";

    private EsiosClient esiosClient;
    private MockRestServiceServer mockServer;

    private static final String INDICATOR_RESPONSE = """
            {
              "id": 600,
              "name": "Test indicator"
            }
            """;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        mockServer = MockRestServiceServer.bindTo(builder).build();
        EsiosProperties properties = new EsiosProperties(true, URI.create(BASE_URL), TOKEN);
        esiosClient = new EsiosClient(builder, properties, new ClientExceptionHandler());
    }

    @Test
    void testRetrieveIndicator() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery(600, null, null);
        expectIndicatorRequest("/indicators/600");

        // When
        Indicator result = esiosClient.getIndicatorTariff(query);

        // Then
        assertNotNull(result);
        assertEquals(600, result.getId());

        mockServer.verify();
    }

    @Test
    void testLanguageFilter() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery(
                600,
                Language.EN,
                null
        );

        expectIndicatorRequest("/indicators/600?locale=en");

        // When
        esiosClient.getIndicatorTariff(query);

        // Then
        mockServer.verify();
    }

    @Test
    void testTimeFilters() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery(
                600,
                null,
                OffsetDateTime.parse("2026-11-10T10:00:00Z")
        );

        expectIndicatorRequest(
                "/indicators/600"
                + "?datetime=2026-11-10T10:00Z"
        );

        // When
        esiosClient.getIndicatorTariff(query);

        // Then
        mockServer.verify();
    }

    @Test
    void testTimeAggregationFilters() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery(
                600,
                null,
                null,
                null,
                Aggregation.AVERAGE,
                TimeTruncation.HOUR,
                null,
                null,
                null
        );

        expectIndicatorRequest("/indicators/600?time_agg=avg&time_trunc=hour");

        // When
        esiosClient.getIndicatorTariff(query);

        // Then
        mockServer.verify();
    }

    @Test
    void testGeoFilters() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery(
                600,
                null,
                null,
                null,
                null,
                null,
                List.of(1, 2),
                Aggregation.SUM,
                GeoTruncation.COUNTRY
        );

        expectIndicatorRequest("/indicators/600?geo_ids%5B%5D=1&geo_ids%5B%5D=2&geo_agg=sum&geo_trunc=country");

        // When
        Indicator result = esiosClient.getIndicatorTariff(query);

        // Then
        assertNotNull(result);
        mockServer.verify();
    }

    @Test
    void testAllFilters() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery(
                600,
                Language.EN,
                OffsetDateTime.parse("2026-11-01T00:00:00Z"),
                OffsetDateTime.parse("2026-11-10T23:59:59Z"),
                Aggregation.AVERAGE,
                TimeTruncation.HOUR,
                List.of(1, 2),
                Aggregation.SUM,
                GeoTruncation.COUNTRY
        );

        expectIndicatorRequest(
                "/indicators/600?locale=en"
                + "&start_date=2026-11-01T00:00Z"
                + "&end_date=2026-11-10T23:59:59Z"
                + "&time_agg=avg"
                + "&time_trunc=hour"
                + "&geo_ids%5B%5D=1"
                + "&geo_ids%5B%5D=2"
                + "&geo_agg=sum"
                + "&geo_trunc=country"
        );

        // When
        esiosClient.getIndicatorTariff(query);

        // Then
        mockServer.verify();
    }

    private void expectIndicatorRequest(String expectedUri) {
        mockServer.expect(requestTo(BASE_URL + expectedUri))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                  .andRespond(withSuccess(INDICATOR_RESPONSE, MediaType.APPLICATION_JSON));
    }
}
