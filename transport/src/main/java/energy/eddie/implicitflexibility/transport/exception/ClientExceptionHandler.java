package energy.eddie.implicitflexibility.transport.exception;

import energy.eddie.implicitflexibility.interactions.exception.DataSourceNotFoundException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceServerException;
import energy.eddie.implicitflexibility.interactions.exception.DataSourceUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.function.Supplier;

@Component
public class ClientExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ClientExceptionHandler.class);

    public <T> T execute(String operation, Supplier<T> action, String dataSource) {
        try {
            return action.get();
        } catch (HttpClientErrorException.NotFound e) {
            LOG.error("Data not found while retrieving {} from data source {}",
                      operation, dataSource, e);

            throw new DataSourceNotFoundException(dataSource,
                                                  "The requested data was not found.", e);
        } catch (HttpClientErrorException.Forbidden e) {
            LOG.error("Data source server forbidden access while retrieving {} from data source {}",
                      operation, dataSource, e);

            throw new DataSourceServerException(dataSource,
                                                "Access to the data source forbidden.", e);
        } catch (HttpServerErrorException e) {
            LOG.error("Data source server error while retrieving {} from data source {}",
                      operation, dataSource, e);

            throw new DataSourceServerException(dataSource,
                                                "The requested data could not be retrieved from the data source.", e);
        } catch (ResourceAccessException e) {
            LOG.error("Data source unavailable while retrieving {} from data source {}",
                      operation, dataSource, e);

            throw new DataSourceUnavailableException(dataSource,
                                                     "The data source is currently unavailable.", e);
        }
    }
}
