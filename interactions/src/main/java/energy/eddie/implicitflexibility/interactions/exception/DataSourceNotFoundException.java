package energy.eddie.implicitflexibility.interactions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataSourceNotFoundException extends DataSourceException {
    public DataSourceNotFoundException(String dataSourceId, String message) {
        super(dataSourceId, message);
    }
}
