package energy.eddie.implicitflexibility.interactions.exception;

public class DataSourceNotFoundException extends DataSourceException {

    public DataSourceNotFoundException(String dataSourceId, String message, Throwable cause) {
        super(dataSourceId, message, cause);
    }
}
