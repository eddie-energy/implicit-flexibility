package energy.eddie.implicitflexibility.datasource.es.esios.models;

import java.util.List;

public record IndicatorList(List<IndicatorSpecification> indicators, IndicatorMeta meta) {
}
