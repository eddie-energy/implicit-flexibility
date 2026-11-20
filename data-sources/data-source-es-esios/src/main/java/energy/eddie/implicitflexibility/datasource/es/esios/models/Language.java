package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnum;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnumUtils;

public enum Language implements EsiosEnum {
    ES("es"),
    EN("en");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Language fromValue(String value) {
        return EsiosEnumUtils.fromValue(Language.class, value);
    }
}
