package energy.eddie.implicitflexibility.interactions.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class DataSourceResponseErrorHandler extends DefaultResponseErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceResponseErrorHandler.class);
    private final String dataSourceId;

    public DataSourceResponseErrorHandler(String dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response)
            throws IOException {

        HttpStatusCode status = response.getStatusCode();
        String body = new String(getResponseBody(response), StandardCharsets.UTF_8);
        HttpStatus httpStatus = HttpStatus.valueOf(status.value());
        String message = "Data source '%s' returned HTTP %d for %s %s: %s"
                .formatted(dataSourceId, status.value(), method, url, body);

        DataSourceException exception = switch (httpStatus) {
            case NOT_FOUND -> new DataSourceNotFoundException(dataSourceId, message);
            case SERVICE_UNAVAILABLE -> new DataSourceUnavailableException(dataSourceId, message);
            default -> new DataSourceServerException(dataSourceId, message);
        };

        LOG.error("Data source request failed", exception);
        throw exception;
    }
}
