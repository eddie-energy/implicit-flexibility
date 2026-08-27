package energy.eddie.implicitflexibility.interactions.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataSourceRegistry {

    private static final Logger log = LoggerFactory.getLogger(DataSourceRegistry.class);
    private final Map<String, DataSource> enabledDataSources;

    public DataSourceRegistry(List<DataSource> dataSources, Environment environment) {

        log.info("Discovering data sources...");
        this.enabledDataSources = dataSources.stream()
                .filter(dataSource -> {
                    String propertyName = "data-source.%s.enabled".formatted(dataSource.getId());
                    boolean enabled = environment.getProperty(propertyName, Boolean.class, false);
                    log.info("Data source '{}' - {}", dataSource.getId(), enabled ? "ENABLED" : "DISABLED");
                    return enabled;
                })
                .collect(Collectors.toUnmodifiableMap(
                        DataSource::getCountry,
                        Function.identity()
                ));
    }

    public Collection<DataSource> getAll() {
        return enabledDataSources.values();
    }
}
