package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public record IndicatorItem(@JsonAlias("geo_name") String name,
                            @JsonAlias("geo_id") long id) implements IndicatorBase {
}
