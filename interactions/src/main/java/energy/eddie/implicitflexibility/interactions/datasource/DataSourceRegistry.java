package energy.eddie.implicitflexibility.interactions.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataSourceRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceRegistry.class);
    private final Map<String, DataSource> enabledDataSources;

    public DataSourceRegistry(List<DataSource> dataSources) {
        this.enabledDataSources = dataSources.stream()
                                             .collect(Collectors.toUnmodifiableMap(
                                                     DataSource::getId,
                                                     Function.identity()
                                             ));

        if (dataSources.isEmpty()) {
            LOG.warn("No data sources are enabled.");
        } else {
            dataSources.stream()
                       .map(DataSource::getId)
                       .sorted()
                       .forEach(id -> LOG.info("Data source enabled: {}", id));
        }
    }

    public Collection<DataSource> getAll() {
        return enabledDataSources.values();
    }

    public Collection<DataSource> getByCountry(String country) {
        return enabledDataSources.values().stream()
                                 .filter(dataSource -> dataSource.getCountry().equals(country))
                                 .toList();
    }
}
