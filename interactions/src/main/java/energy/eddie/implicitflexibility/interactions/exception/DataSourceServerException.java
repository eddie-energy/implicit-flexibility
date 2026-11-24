package energy.eddie.implicitflexibility.interactions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class DataSourceServerException extends DataSourceException {
    public DataSourceServerException(String dataSourceId, String message) {
        super(dataSourceId, message);
    }
}
