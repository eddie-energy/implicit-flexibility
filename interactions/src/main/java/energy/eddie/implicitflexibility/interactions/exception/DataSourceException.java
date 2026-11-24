package energy.eddie.implicitflexibility.interactions.exception;

public abstract class DataSourceException extends RuntimeException {
    private final String dataSourceId;


    protected DataSourceException(String dataSourceId, String message) {
        super(message);
        this.dataSourceId = dataSourceId;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }
}
