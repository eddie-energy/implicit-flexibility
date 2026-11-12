package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;

public class IndicatorValue extends IndicatorBase {
    private final BigDecimal value;

    private final OffsetDateTime datetime;

    @JsonProperty("datetime_utc")
    private final Instant datetimeUtc;

    @JsonProperty("tz_time")
    private final Instant tzTime;

    IndicatorValue(
            String name,
            long id,
            BigDecimal value,
            OffsetDateTime datetime,
            Instant datetimeUtc,
            Instant tzTime
    ) {
        super(name, id);
        this.value = value;
        this.datetime = datetime;
        this.datetimeUtc = datetimeUtc;
        this.tzTime = tzTime;
    }

    public BigDecimal getValue() {
        return value;
    }

    public OffsetDateTime getDatetime() {
        return datetime;
    }

    public Instant getDatetimeUtc() {
        return datetimeUtc;
    }

    public Instant getTzTime() {
        return tzTime;
    }
}
