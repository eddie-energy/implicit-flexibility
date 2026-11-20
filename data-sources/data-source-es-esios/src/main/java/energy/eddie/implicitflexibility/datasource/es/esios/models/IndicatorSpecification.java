package energy.eddie.implicitflexibility.datasource.es.esios.models;

public class IndicatorSpecification extends IndicatorItem {
    private final String description;

    public IndicatorSpecification(String name, long id, String shortName, String description) {
        super(name, id, shortName);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
