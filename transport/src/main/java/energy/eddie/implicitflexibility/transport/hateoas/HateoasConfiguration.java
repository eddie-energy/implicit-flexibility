package energy.eddie.implicitflexibility.transport.hateoas;

import energy.eddie.implicitflexibility.interactions.hateoas.FormTypeProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;

import java.util.List;

@Configuration
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL_FORMS)
public class HateoasConfiguration {

    @Bean
    HalFormsConfiguration halFormsConfiguration(List<FormTypeProvider> providers) {

        HalFormsConfiguration configuration = new HalFormsConfiguration();

        for (FormTypeProvider provider : providers) {
            for (Class<?> formType : provider.getFormTypes()) {
                configuration = HalFormsEnumOptions.register(configuration, formType);
            }
        }

        return configuration;
    }
}