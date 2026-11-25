package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

public record IndicatorValue(
        @JsonAlias("geo_name") String name,
        @JsonAlias("geo_id") long id,
        BigDecimal value,
        OffsetDateTime datetime,
        @JsonProperty("datetime_utc") Instant datetimeUtc,
        @JsonProperty("tz_time") Instant tzTime
) implements IndicatorBase {
}
