package energy.eddie.implicitflexibility.interactions.exception;

public class DataSourceUnavailableException extends DataSourceException {

    public DataSourceUnavailableException(String dataSourceId, String message, Throwable cause) {
        super(dataSourceId, message, cause);
    }
}
