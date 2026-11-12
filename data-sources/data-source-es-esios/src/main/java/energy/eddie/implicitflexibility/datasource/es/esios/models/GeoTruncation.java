package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnum;
import energy.eddie.implicitflexibility.datasource.es.esios.query.EsiosEnumUtils;

public enum GeoTruncation implements EsiosEnum {

    COUNTRY("country"),
    ELECTRIC_SYSTEM("electric_system"),
    ELECTRIC_SYSTEM_PENINSULA("electric_system_peninsula"),
    ELECTRIC_SYSTEM_CANARY("electric_system_canary"),
    ELECTRIC_SYSTEM_BALEARIC("electric_system_balearic"),
    ELECTRIC_SYSTEM_CEUTA("electric_system_ceuta"),
    ELECTRIC_SYSTEM_MELILLA("electric_system_melilla"),
    ELECTRIC_SYSTEM_PENINSULA_CCAA("electric_system_peninsula_ccaa"),
    ELECTRIC_SYSTEM_CANARY_CCAA("electric_system_canary_ccaa"),
    ELECTRIC_SYSTEM_BALEARIC_CCAA("electric_system_balearic_ccaa"),
    ELECTRIC_SYSTEM_CEUTA_CCAA("electric_system_ceuta_ccaa"),
    ELECTRIC_SYSTEM_MELILLA_CCAA("electric_system_melilla_ccaa"),
    AUTONOMOUS_COMMUNITY("autonomous_community"),
    PROVINCE("province"),
    ELECTRIC_SUBSYSTEM("electric_subsystem"),
    TOWN("town"),
    DRAINAGE_BASIN("drainage_basin");

    private final String value;

    GeoTruncation(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static GeoTruncation fromValue(String value) {
        return EsiosEnumUtils.fromValue(GeoTruncation.class, value);
    }
}
