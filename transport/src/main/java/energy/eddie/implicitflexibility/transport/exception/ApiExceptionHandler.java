package energy.eddie.implicitflexibility.transport.exception;

import energy.eddie.implicitflexibility.interactions.exception.DataSourceNotFoundException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceServerException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DataSourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleDataSourceNotFound() {
        var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Data not found");
        problem.setDetail("The requested data was not found.");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(problem);
    }

    @ExceptionHandler(DataSourceServerException.class)
    public ResponseEntity<ProblemDetail> handleDataSourceServerError() {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle("Data source error");
        problem.setDetail("The requested data could not be retrieved from the data source.");

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(problem);
    }

    @ExceptionHandler(DataSourceUnavailableException.class)
    public ResponseEntity<ProblemDetail> handleDataSourceUnavailable() {
        var problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle("Data source unavailable");
        problem.setDetail("The requested data could not be retrieved because the data source is unavailable.");

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(problem);
    }
}
