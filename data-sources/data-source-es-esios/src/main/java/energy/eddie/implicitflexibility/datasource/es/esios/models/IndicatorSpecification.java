package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IndicatorSpecification(
        String name,
        long id,
        @JsonProperty("short_name") String shortName,
        String description
) implements IndicatorBase {
}
