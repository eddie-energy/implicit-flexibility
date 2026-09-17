package energy.eddie.implicitflexibility.interactions.datasource;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataSourceRegistryTest {

    @Test
    void registerDataSources() {
        // Given
        DataSource atEControl = dataSource("at.e-control", "AT");
        DataSource dkExample = dataSource("dk.example", "DK");

        // When
        DataSourceRegistry registry = new DataSourceRegistry(List.of(atEControl, dkExample));
        Collection<DataSource> result = registry.getAll();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(atEControl));
        assertTrue(result.contains(dkExample));
    }

    @Test
    void registerDataSources_SameCountry() {
        // Given
        DataSource eControl = dataSource("at.e-control", "AT");
        DataSource other = dataSource("at.other", "AT");

        // When
        DataSourceRegistry registry = new DataSourceRegistry(List.of(eControl, other));
        Collection<DataSource> result = registry.getByCountry("AT");

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(eControl));
        assertTrue(result.contains(other));
    }

    @Test
    void getDataSourcesByCountry() {
        // Given
        DataSource atEControl = dataSource("at.e-control", "AT");
        DataSource dkExample = dataSource("dk.example", "DK");

        // When
        DataSourceRegistry registry = new DataSourceRegistry(List.of(atEControl, dkExample));
        Collection<DataSource> result = registry.getByCountry("AT");

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(atEControl));
        assertFalse(result.contains(dkExample));
    }

    @Test
    void getDataSources_UnknownCountry() {
        // Given
        DataSource atEControl = dataSource("at.e-control", "AT");

        // When
        DataSourceRegistry registry = new DataSourceRegistry(List.of(atEControl));
        Collection<DataSource> result = registry.getByCountry("DK");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDataSources_DuplicateIds() {
        // Given
        DataSource first = dataSource("at.e-control", "AT");
        DataSource second = dataSource("at.e-control", "DK");
        List<DataSource> dataSources = List.of(first, second);

        // When / Then
        assertThrows(IllegalStateException.class, () -> new DataSourceRegistry(dataSources));
    }

    @Test
    void getDataSources_NoDataSources() {
        // Given
        List<DataSource> dataSources = List.of();

        // When
        DataSourceRegistry registry = new DataSourceRegistry(dataSources);

        // Then
        assertNotNull(registry.getAll());
        assertTrue(registry.getAll().isEmpty());
    }

    @Test
    void dataSourceInstances() {
        // Given
        DataSource dataSource = dataSource("at.e-control", "AT");

        // When
        DataSourceRegistry registry = new DataSourceRegistry(List.of(dataSource));
        DataSource result = registry.getAll().iterator().next();

        // Then
        assertSame(dataSource, result);
    }

    private DataSource dataSource(String id, String country) {
        DataSource dataSource = mock(DataSource.class);

        when(dataSource.getId()).thenReturn(id);
        when(dataSource.getCountry()).thenReturn(country);

        return dataSource;
    }
}
