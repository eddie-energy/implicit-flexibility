package energy.eddie.implicitflexibility.interactions.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataSourceResponseErrorHandlerTest {

    private static final String DATA_SOURCE_ID = "xx.example";
    private static final URI URL = URI.create("test-url");
    private static final HttpMethod METHOD = HttpMethod.GET;

    private final DataSourceResponseErrorHandler handler =
            new DataSourceResponseErrorHandler(DATA_SOURCE_ID);

    @Mock
    private ClientHttpResponse response;

    @Test
    void handleError_DataSourceNotFoundException() throws IOException {
        // Given
        assertHandlerThrows(
                HttpStatus.NOT_FOUND,
                "Product not found",
                DataSourceNotFoundException.class
        );
    }

    @Test
    void handleError_DataSourceUnavailableException() throws IOException {
        // Given
        assertHandlerThrows(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service unavailable",
                DataSourceUnavailableException.class
        );
    }

    @Test
    void handleError_DataSourceServerException() throws IOException {
        // Given
        assertHandlerThrows(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                DataSourceServerException.class
        );
    }

    private <T extends DataSourceException> void assertHandlerThrows(
            HttpStatus status,
            String body,
            Class<T> expectedException
    ) throws IOException {

        // Given
        mockResponse(status, body);

        // When
        var exception = assertThrows(
                expectedException,
                () -> handler.handleError(URL, METHOD, response)
        );

        // Then
        assertEquals(DATA_SOURCE_ID, exception.getDataSourceId());

        var message = Objects.requireNonNull(exception.getMessage());
        assertTrue(message.contains("HTTP " + status.value()));
        assertTrue(message.contains(body));
    }

    private void mockResponse(HttpStatus status, String body) throws IOException {
        when(response.getStatusCode()).thenReturn(status);
        when(response.getBody()).thenReturn(new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8)));
    }
}
