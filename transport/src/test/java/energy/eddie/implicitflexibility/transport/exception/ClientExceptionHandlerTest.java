package energy.eddie.implicitflexibility.transport.exception;

import energy.eddie.implicitflexibility.interactions.exception.DataSourceNotFoundException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceServerException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceUnavailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class ClientExceptionHandlerTest {
    private static final String OPERATION = "indicator";
    private static final String DATA_SOURCE = "es.esios";
    private static final String RESULT = "result";

    private ClientExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ClientExceptionHandler();
    }

    @Test
    void testExecute() {
        // Given
        Supplier<String> action = () -> RESULT;

        // When
        String result = handler.execute(OPERATION, action, DATA_SOURCE);

        // Then
        assertEquals(RESULT, result);
    }

    @Test
    void testExecute_NotFoundException() {
        // Given
        var cause = HttpClientErrorException.create(
                HttpClientErrorException.NotFound.class.getSimpleName(),
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                null,
                null
        );

        Supplier<String> action = () -> {
            throw cause;
        };

        // When
        DataSourceNotFoundException exception = assertThrows(
                DataSourceNotFoundException.class,
                () -> handler.execute(OPERATION, action, DATA_SOURCE)
        );

        // Then
        assertSame(cause, exception.getCause());
    }

    @Test
    void testExecute_ForbiddenException() {
        // Given
        var cause = HttpClientErrorException.create(
                HttpClientErrorException.Forbidden.class.getSimpleName(),
                HttpStatus.FORBIDDEN,
                "Forbidden",
                HttpHeaders.EMPTY,
                null,
                null
        );

        Supplier<String> action = () -> {
            throw cause;
        };

        // When
        DataSourceServerException exception = assertThrows(
                DataSourceServerException.class,
                () -> handler.execute(OPERATION, action, DATA_SOURCE)
        );

        // Then
        assertSame(cause, exception.getCause());
    }

    @Test
    void testExecute_ServerErrorException() {
        // Given
        HttpServerErrorException cause =
                HttpServerErrorException.create(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        HttpHeaders.EMPTY,
                        new byte[0],
                        null
                );

        Supplier<String> action = () -> {
            throw cause;
        };

        // When
        DataSourceServerException exception = assertThrows(
                DataSourceServerException.class,
                () -> handler.execute(OPERATION, action, DATA_SOURCE)
        );

        // Then
        assertSame(cause, exception.getCause());
    }

    @Test
    void testExecute_ResourceAccessException() {
        // Given
        ResourceAccessException cause = new ResourceAccessException("Connection refused");

        Supplier<String> action = () -> {
            throw cause;
        };

        // When
        DataSourceUnavailableException exception = assertThrows(
                DataSourceUnavailableException.class,
                () -> handler.execute(OPERATION, action, DATA_SOURCE)
        );

        // Then
        assertSame(cause, exception.getCause());
    }
}
