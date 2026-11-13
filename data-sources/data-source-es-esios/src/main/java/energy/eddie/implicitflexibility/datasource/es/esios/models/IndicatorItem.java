package energy.eddie.implicitflexibility.datasource.es.esios.models;

abstract class IndicatorItem extends IndicatorBase {
    private final String shortName;

    IndicatorItem(String name, long id, String shortName) {
        super(name, id);
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
