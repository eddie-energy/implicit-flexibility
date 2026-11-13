package energy.eddie.implicitflexibility.datasource.es.esios.query;

import energy.eddie.implicitflexibility.datasource.es.esios.models.Aggregation;
import energy.eddie.implicitflexibility.datasource.es.esios.models.GeoTruncation;
import energy.eddie.implicitflexibility.datasource.es.esios.models.Language;
import energy.eddie.implicitflexibility.datasource.es.esios.models.TimeTruncation;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EsiosTariffQueryTest {
    private static final OffsetDateTime DATETIME = OffsetDateTime.parse("2026-11-13T10:00:00Z");

    @Test
    void testNoArgsConstructor() {
        // Given

        // When
        EsiosTariffQuery query = new EsiosTariffQuery();

        // Then
        assertAll(
                () -> assertEquals(0, query.getIndicatorId()),
                () -> assertNull(query.getLanguage()),
                () -> assertNull(query.getDatetime()),
                () -> assertNull(query.getStartDate()),
                () -> assertNull(query.getEndDate()),
                () -> assertNull(query.getTimeAggregation()),
                () -> assertNull(query.getTimeTruncation()),
                () -> assertNull(query.getGeoIds()),
                () -> assertNull(query.getGeoAggregation()),
                () -> assertNull(query.getGeoTruncation())
        );
    }

    @Test
    void testFullConstructor() {
        // Given
        int indicatorId = 600;
        Language language = Language.EN;
        OffsetDateTime startDate = DATETIME.minusDays(1);
        OffsetDateTime endDate = DATETIME;
        Aggregation timeAggregation = Aggregation.SUM;
        TimeTruncation timeTruncation = TimeTruncation.DAY;
        List<Integer> geoIds = List.of(1, 2);
        Aggregation geoAggregation = Aggregation.AVERAGE;
        GeoTruncation geoTruncation = GeoTruncation.COUNTRY;

        // When
        EsiosTariffQuery query = new EsiosTariffQuery(
                indicatorId,
                language,
                startDate,
                endDate,
                timeAggregation,
                timeTruncation,
                geoIds,
                geoAggregation,
                geoTruncation
        );

        // Then
        assertAll(
                () -> assertEquals(indicatorId, query.getIndicatorId()),
                () -> assertEquals(language, query.getLanguage()),
                () -> assertEquals(startDate, query.getStartDate()),
                () -> assertEquals(endDate, query.getEndDate()),
                () -> assertEquals(timeAggregation, query.getTimeAggregation()),
                () -> assertEquals(timeTruncation, query.getTimeTruncation()),
                () -> assertEquals(geoIds, query.getGeoIds()),
                () -> assertEquals(geoAggregation, query.getGeoAggregation()),
                () -> assertEquals(geoTruncation, query.getGeoTruncation())
        );
    }

    @Test
    void testDatetimeConstructor() {
        // Given
        int indicatorId = 600;
        Language language = Language.EN;

        // When
        EsiosTariffQuery query = new EsiosTariffQuery(indicatorId, language, DATETIME);

        // Then
        assertAll(
                () -> assertEquals(indicatorId, query.getIndicatorId()),
                () -> assertEquals(language, query.getLanguage()),
                () -> assertEquals(DATETIME, query.getDatetime()),
                () -> assertNull(query.getStartDate()),
                () -> assertNull(query.getEndDate())
        );
    }

    @Test
    void testSetters() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery();

        Language language = Language.EN;
        Aggregation aggregation = Aggregation.SUM;
        TimeTruncation timeTruncation = TimeTruncation.DAY;
        GeoTruncation geoTruncation = GeoTruncation.COUNTRY;
        List<Integer> geoIds = List.of(1, 2);

        // When
        query.setIndicatorId(600);
        query.setLanguage(language);
        query.setDatetime(DATETIME);
        query.setStartDate(DATETIME.minusDays(1));
        query.setEndDate(DATETIME);
        query.setTimeAggregation(aggregation);
        query.setTimeTruncation(timeTruncation);
        query.setGeoIds(geoIds);
        query.setGeoAggregation(aggregation);
        query.setGeoTruncation(geoTruncation);

        // Then
        assertAll(
                () -> assertEquals(600, query.getIndicatorId()),
                () -> assertEquals(language, query.getLanguage()),
                () -> assertEquals(DATETIME, query.getDatetime()),
                () -> assertEquals(DATETIME.minusDays(1), query.getStartDate()),
                () -> assertEquals(DATETIME, query.getEndDate()),
                () -> assertEquals(aggregation, query.getTimeAggregation()),
                () -> assertEquals(timeTruncation, query.getTimeTruncation()),
                () -> assertEquals(geoIds, query.getGeoIds()),
                () -> assertEquals(aggregation, query.getGeoAggregation()),
                () -> assertEquals(geoTruncation, query.getGeoTruncation())
        );
    }
}
