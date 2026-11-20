package energy.eddie.implicitflexibility.datasource.es.esios.models;

import com.fasterxml.jackson.annotation.JsonAlias;

public class IndicatorBase {
    private final String name;
    private final long id;

    public IndicatorBase(@JsonAlias("geo_name") String name, @JsonAlias("geo_id") long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }
}
