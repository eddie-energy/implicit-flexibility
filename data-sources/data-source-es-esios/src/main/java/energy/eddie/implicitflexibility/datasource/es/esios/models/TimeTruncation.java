package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnum;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnumUtils;

public enum TimeTruncation implements EsiosEnum {
    FIVE_MINUTES("five_minutes"),
    TEN_MINUTES("ten_minutes"),
    FIFTEEN_MINUTES("fifteen_minutes"),
    HOUR("hour"),
    DAY("day"),
    MONTH("month"),
    YEAR("year");

    private final String value;

    TimeTruncation(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TimeTruncation fromValue(String value) {
        return EsiosEnumUtils.fromValue(TimeTruncation.class, value);
    }
}
