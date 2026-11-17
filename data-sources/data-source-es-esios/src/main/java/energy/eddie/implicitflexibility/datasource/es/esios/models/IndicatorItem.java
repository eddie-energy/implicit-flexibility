package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonProperty;

abstract class IndicatorItem extends IndicatorBase {
    
    @JsonProperty("short_name")
    private final String shortName;

    IndicatorItem(String name, long id, String shortName) {
        super(name, id);
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
