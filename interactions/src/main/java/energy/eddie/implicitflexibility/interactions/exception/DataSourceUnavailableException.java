package energy.eddie.implicitflexibility.interactions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class DataSourceUnavailableException extends DataSourceException {
    public DataSourceUnavailableException(String dataSourceId, String message) {
        super(dataSourceId, message);
    }
}
