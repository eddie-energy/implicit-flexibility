package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record Indicator(
        String name,
        long id,
        @JsonProperty("short_name") String shortName,
        Boolean composited,
        @JsonProperty("step_type") String stepType,
        Boolean disaggregated,
        List<IndicatorItem> geos,
        List<IndicatorItem> magnitud,
        List<IndicatorItem> tiempo,
        @JsonProperty("values_updated_at") OffsetDateTime valuesUpdatedAt,
        Integer valuesSum,
        List<IndicatorValue> values
) implements IndicatorBase {
}
