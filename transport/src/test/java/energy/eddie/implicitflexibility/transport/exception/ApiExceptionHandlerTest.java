package energy.eddie.implicitflexibility.transport.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerTest {

    private ApiExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ApiExceptionHandler();
    }

    @Test
    void testHandleNotFound() {
        // Given
        var exception = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        // When
        var response = handler.handleNotFound(
                (HttpClientErrorException.NotFound) exception
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertProblemDetail(
                response,
                HttpStatus.NOT_FOUND,
                "Data not found",
                "The requested data was not found."
        );
    }

    @Test
    void testHandleUpstreamClientError() {
        // Given
        var exception = HttpClientErrorException.create(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        // When
        var response = handler.handleUpstreamHttpError(exception);

        // Then
        assertProblemDetail(
                response,
                HttpStatus.BAD_GATEWAY,
                "Data source error",
                "The data source could not successfully process the request."
        );
    }

    @Test
    void testHandleUpstreamServerError() {
        // Given
        var exception = HttpServerErrorException.create(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );

        // When
        var response = handler.handleUpstreamHttpError(exception);

        // Then
        assertProblemDetail(
                response,
                HttpStatus.BAD_GATEWAY,
                "Data source error",
                "The data source could not successfully process the request."
        );
    }

    @Test
    void testHandleNetworkingError() {
        // Given
        var exception = new ResourceAccessException("Connection refused");

        // When
        var response = handler.handleNetworkingError(exception);

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertProblemDetail(
                response,
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service unavailable",
                "The service is temporarily unavailable."
        );
    }

    private void assertProblemDetail(
            ResponseEntity<ProblemDetail> response,
            HttpStatus expectedStatus,
            String expectedTitle,
            String expectedDetail
    ) {
        ProblemDetail problem = Objects.requireNonNull(response.getBody());

        assertEquals(expectedStatus.value(), response.getStatusCode().value());
        assertEquals(expectedStatus.value(), problem.getStatus());
        assertEquals(expectedTitle, problem.getTitle());
        assertEquals(expectedDetail, problem.getDetail());
    }
}
