package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public class Indicator extends IndicatorItem {

    private final Boolean composited;
    private final String stepType;
    private final Boolean disaggregated;
    private final List<IndicatorBase> geos;
    private final List<IndicatorBase> magnitud;
    private final List<IndicatorBase> tiempo;
    @JsonProperty("values_updated_at")
    private final OffsetDateTime valuesUpdatedAt;
    private final List<IndicatorValue> values;

    public Indicator(
            String name,
            long id,
            String shortName,
            Boolean composited,
            String stepType,
            Boolean disaggregated,
            List<IndicatorBase> geos,
            List<IndicatorBase> magnitud,
            List<IndicatorBase> tiempo,
            OffsetDateTime valuesUpdatedAt,
            List<IndicatorValue> values
    ) {
        super(name, id, shortName);
        this.composited = composited;
        this.stepType = stepType;
        this.disaggregated = disaggregated;
        this.geos = geos;
        this.magnitud = magnitud;
        this.tiempo = tiempo;
        this.valuesUpdatedAt = valuesUpdatedAt;
        this.values = values;
    }

    public Boolean getComposited() {
        return composited;
    }

    public String getStepType() {
        return stepType;
    }

    public Boolean isDisaggregated() {
        return disaggregated;
    }

    public List<IndicatorBase> getGeos() {
        return geos;
    }

    public List<IndicatorBase> getMagnitud() {
        return magnitud;
    }

    public List<IndicatorBase> getTiempo() {
        return tiempo;
    }

    public OffsetDateTime getValuesUpdatedAt() {
        return valuesUpdatedAt;
    }

    public List<IndicatorValue> getValues() {
        return values;
    }
}
