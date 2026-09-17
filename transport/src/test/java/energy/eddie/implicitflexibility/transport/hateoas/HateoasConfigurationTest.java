package energy.eddie.implicitflexibility.transport.hateoas;

import energy.eddie.implicitflexibility.interactions.hateoas.FormTypeProvider;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HateoasConfigurationTest {

    @Test
    void testHalFormsConfiguration() {
        // Given
        FormTypeProvider provider = mock(FormTypeProvider.class);
        when(provider.getFormTypes()).thenReturn(List.of());
        HateoasConfiguration hateoasConfiguration = new HateoasConfiguration();

        // When
        HalFormsConfiguration result = hateoasConfiguration.halFormsConfiguration(List.of(provider));

        // Then
        assertNotNull(result);
    }
}
