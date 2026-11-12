package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnum;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnumUtils;

public enum Aggregation implements EsiosEnum {
    SUM("sum"),
    AVERAGE("avg");

    private final String value;

    Aggregation(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Aggregation fromValue(String value) {
        return EsiosEnumUtils.fromValue(Aggregation.class, value);
    }
}
