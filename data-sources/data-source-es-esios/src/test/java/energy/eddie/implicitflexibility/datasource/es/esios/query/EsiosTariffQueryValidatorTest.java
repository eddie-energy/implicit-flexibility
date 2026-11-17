package energy.eddie.implicitflexibility.datasource.es.esios.query;

import energy.eddie.implicitflexibility.datasource.es.esios.models.Language;
import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EsiosTariffQueryValidatorTest {

    private ValidatorFactory factory;
    private Validator validator;

    private static final OffsetDateTime START_DATE = OffsetDateTime.parse("2026-11-01T00:00:00Z");
    private static final OffsetDateTime END_DATE = OffsetDateTime.parse("2026-11-10T23:59:59Z");
    private static final OffsetDateTime DATETIME = OffsetDateTime.parse("2026-11-10T10:00:00Z");

    @BeforeEach
    void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterEach
    void tearDown() {
        factory.close();
    }

    @Test
    void testDatetimeOnly() {
        // Given
        EsiosTariffQuery query = new EsiosTariffQuery();
        query.setIndicatorId(600);
        query.setLanguage(Language.EN);
        query.setDatetime(DATETIME);

        // When
        Set<ConstraintViolation<EsiosTariffQuery>> violations = validator.validate(query);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testStartAndEndDate() {
        // Given
        EsiosTariffQuery query = createIntervalQuery(START_DATE, END_DATE);

        // When
        Set<ConstraintViolation<EsiosTariffQuery>> violations = validator.validate(query);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void testStartDateOnly_shouldReject() {
        assertInvalid(createIntervalQuery(START_DATE, null));
    }

    @Test
    void testEndDateOnly_shouldReject() {
        assertInvalid(createIntervalQuery(null, END_DATE));
    }

    @Test
    void testStartDateAfterEndDate_shouldReject() {
        assertInvalid(createIntervalQuery(
                /* startDate= */ END_DATE,
                /* endDate= */ START_DATE
        ));
    }

    @Test
    void testStartDateEqualToEndDate() {
        assertValid(createIntervalQuery(START_DATE, START_DATE));
    }

    private EsiosTariffQuery createIntervalQuery(
            @Nullable OffsetDateTime startDate,
            @Nullable OffsetDateTime endDate
    ) {
        var query = new EsiosTariffQuery();
        query.setIndicatorId(600);
        query.setLanguage(Language.EN);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        return query;
    }

    private void assertValid(EsiosTariffQuery query) {
        assertTrue(validator.validate(query).isEmpty());
    }

    private void assertInvalid(EsiosTariffQuery query) {
        assertFalse(validator.validate(query).isEmpty());
    }
}
