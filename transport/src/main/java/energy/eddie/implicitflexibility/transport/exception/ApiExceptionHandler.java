package energy.eddie.implicitflexibility.transport.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ProblemDetail> handleNotFound(HttpClientErrorException.NotFound exception) {
        LOG.warn("Upstream server returned a not found error", exception);
        return createResponse(
                HttpStatus.NOT_FOUND,
                "Data not found",
                "The requested data was not found."
        );
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ProblemDetail> handleUpstreamHttpError(RestClientResponseException exception) {
        LOG.error("Upstream HTTP request failed with status {}", exception.getStatusCode(), exception);
        return createResponse(
                HttpStatus.BAD_GATEWAY,
                "Data source error",
                "The data source could not successfully process the request."
        );
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ProblemDetail> handleNetworkingError(ResourceAccessException exception) {
        LOG.error("Error communicating with an upstream server", exception);
        return createResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service unavailable",
                "The service is temporarily unavailable."
        );
    }

    private ResponseEntity<ProblemDetail> createResponse(HttpStatus status, String title, String detail) {
        var problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);

        return ResponseEntity.status(status).body(problem);
    }
}
