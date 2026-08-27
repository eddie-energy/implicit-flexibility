package energy.eddie.implicitflexibility.datasource.at.econtrol.config;

import energy.eddie.implicitflexibility.datasource.at.econtrol.tariff.EControlInformationType;
import energy.eddie.implicitflexibility.datasource.at.econtrol.tariff.EControlTariffQuery;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;

@Configuration
public class EControlHalFormsConfiguration {

    @Bean
    HalFormsConfiguration eControlHalFormsConfiguration() {

        return new HalFormsConfiguration()
                .withOptions(EControlTariffQuery.class,
                        "informationType",
                        metadata ->
                                HalFormsOptions.inline(
                                        EControlInformationType.CONTRACT,
                                        EControlInformationType.PRICE_INFO
                                )
                );
    }
}
