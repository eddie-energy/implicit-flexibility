package energy.eddie.implicitflexibility.interactions.exception;


public class DataSourceException extends RuntimeException {

    private final String dataSourceId;

    public DataSourceException(String dataSourceId, String message) {
        super(message);
        this.dataSourceId = dataSourceId;
    }

    public DataSourceException(String dataSourceId, String message, Throwable cause) {
        super(message, cause);
        this.dataSourceId = dataSourceId;
    }

    public String getDataSourceId() {
        return dataSourceId;
    }
}
