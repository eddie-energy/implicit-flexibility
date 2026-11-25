package energy.eddie.implicitflexibility.datasource.es.esios;

import energy.eddie.implicitflexibility.datasource.es.esios.config.EsiosProperties;
import energy.eddie.implicitflexibility.datasource.es.esios.models.*;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosTariffQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class EsiosClientTest {
    private static final String BASE_URL = "https://api.esios.ree.es";
    private static final String TOKEN = "test-token";

    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    private EsiosClient esiosClient;
    private MockRestServiceServer mockServer;

    private static final String INDICATOR_RESPONSE = """
            {
                "indicator": {
                    "id": 600,
                    "name": "Test indicator"
                }
            }
            """;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl(BASE_URL);
        mockServer = MockRestServiceServer.bindTo(builder).build();
        EsiosProperties properties = new EsiosProperties(true, URI.create(BASE_URL), TOKEN);
        esiosClient = new EsiosClient(builder, properties, objectMapper);
    }

    @Test
    void testRetrieveIndicator() throws IOException {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery();
        query.setIndicatorId(600);
        InputStream inputStream = Objects.requireNonNull(getClass().getResourceAsStream(
                "/esios/ESIOS_indicator_600.json"));
        String responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        expectIndicatorRequest("/indicators/600", responseBody);

        // When
        Indicator result = esiosClient.getIndicatorTariff(query);

        // Then
        assertNotNull(result);
        assertEquals(600L, result.id());
        assertEquals("Precio mercado SPOT Diario", result.name());
        assertEquals("Mercado SPOT", result.shortName());
        assertEquals(6, result.geos().size());

        mockServer.verify();
    }

    @Test
    void testGetIndicators() throws IOException {
        // Given
        InputStream inputStream = Objects.requireNonNull(getClass().getResourceAsStream(
                "/esios/ESIOS_indicators.json"));
        String responseBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        expectIndicatorRequest("/indicators", responseBody);

        // When
        IndicatorList result = esiosClient.getIndicators();

        // Then
        assertNotNull(result);
        assertNotNull(result.indicators());
        assertFalse(result.indicators().isEmpty());

        IndicatorSpecification indicator = result.indicators().getFirst();

        assertEquals(1L, indicator.id());
        assertEquals("Generación programada PBF Hidráulica UGH", indicator.name());
        assertEquals("Hidráulica UGH", indicator.shortName());
        assertNotNull(indicator.description());

        mockServer.verify();
    }

    @Test
    void testLanguageFilter() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery();
        query.setIndicatorId(600);
        query.setLanguage(Language.EN);

        expectIndicatorRequest("/indicators/600?locale=en");

        // When
        esiosClient.getIndicatorTariff(query);

        // Then
        mockServer.verify();
    }

    @Test
    void testTimeFilters() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery();
        query.setIndicatorId(600);
        query.setDatetime(OffsetDateTime.parse("2026-11-10T10:00:00Z"));

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
        EsiosTariffQuery query = new EsiosTariffQuery();
        query.setIndicatorId(600);
        query.setTimeAggregation(Aggregation.AVERAGE);
        query.setTimeTruncation(TimeTruncation.HOUR);

        expectIndicatorRequest("/indicators/600?time_agg=avg&time_trunc=hour");

        // When
        esiosClient.getIndicatorTariff(query);

        // Then
        mockServer.verify();
    }

    @Test
    void testGeoFilters() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery();
        query.setIndicatorId(600);
        query.setGeoIds(List.of(1, 2));
        query.setGeoAggregation(Aggregation.SUM);
        query.setGeoTruncation(GeoTruncation.COUNTRY);

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
        EsiosTariffQuery query = new EsiosTariffQuery();
        query.setIndicatorId(600);
        query.setLanguage(Language.EN);
        query.setStartDate(OffsetDateTime.parse("2026-11-01T00:00:00Z"));
        query.setEndDate(OffsetDateTime.parse("2026-11-10T23:59:59Z"));
        query.setTimeAggregation(Aggregation.AVERAGE);
        query.setTimeTruncation(TimeTruncation.HOUR);
        query.setGeoIds(List.of(1, 2));
        query.setGeoAggregation(Aggregation.SUM);
        query.setGeoTruncation(GeoTruncation.COUNTRY);

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
        expectIndicatorRequest(expectedUri, INDICATOR_RESPONSE);
    }

    private void expectIndicatorRequest(String expectedUri, String response) {
        mockServer.expect(requestTo(BASE_URL + expectedUri))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                  .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));
    }
}
