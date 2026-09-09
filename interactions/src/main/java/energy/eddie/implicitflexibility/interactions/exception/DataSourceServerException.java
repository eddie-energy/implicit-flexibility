package energy.eddie.implicitflexibility.interactions.exception;

public class DataSourceServerException extends DataSourceException {

    public DataSourceServerException(String dataSourceId, String message, Throwable cause) {
        super(dataSourceId, message, cause);
    }
}
