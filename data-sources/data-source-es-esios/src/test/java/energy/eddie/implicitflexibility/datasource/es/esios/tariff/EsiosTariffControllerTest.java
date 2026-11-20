package energy.eddie.implicitflexibility.datasource.es.esios.tariff;

import energy.eddie.implicitflexibility.datasource.es.esios.EsiosClient;
import energy.eddie.implicitflexibility.datasource.es.esios.EsiosDataSource;
import energy.eddie.implicitflexibility.datasource.es.esios.models.IndicatorList;
import energy.eddie.implicitflexibility.datasource.es.esios.models.IndicatorMeta;
import energy.eddie.implicitflexibility.datasource.es.esios.models.IndicatorSpecification;
import energy.eddie.implicitflexibility.transport.tariff.TariffInformationAffordance;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EsiosTariffControllerTest {

    @Mock
    private EsiosClient client;

    @Mock
    private TariffInformationAffordance tariffInformationAffordance;

    @InjectMocks
    private EsiosTariffController controller;

    @Test
    void testIndicators() {
        // Given
        IndicatorList indicatorList = new IndicatorList(List.of(new IndicatorSpecification(
                "Indicator test name",
                1,
                "Indicator test short",
                "Indicator test description"
        )), new IndicatorMeta(1));
        Link tariffInformationLink = Link.of("/countries/ES/tariffs", "tariff-information");

        doReturn(indicatorList)
                .when(client)
                .getIndicators();

        doReturn(tariffInformationLink)
                .when(tariffInformationAffordance)
                .create(EsiosDataSource.COUNTRY_CODE);

        // When
        EntityModel<IndicatorList> result = controller.indicators();

        // Then
        assertSame(indicatorList, result.getContent());
        assertEquals(tariffInformationLink, result.getLink("tariff-information").orElseThrow());

        verify(client).getIndicators();
        verify(tariffInformationAffordance).create(EsiosDataSource.COUNTRY_CODE);
    }

    @Test
    void testDiscovery() {
        // Given
        String indicators = "indicators";
        Link indicatorsLink = Link.of("/countries/ES/tariffs/discovery/indicators",
                                      indicators);
        doReturn(indicatorsLink)
                .when(tariffInformationAffordance)
                .generateDiscoveryLink(indicators, controller.getClass(), null);

        // When
        RepresentationModel<?> result = controller.discovery();

        // Then
        assertEquals("/countries/ES/tariffs/discovery",
                     result.getRequiredLink("self").getHref());
        assertEquals(indicatorsLink, result.getRequiredLink(indicators));
        verify(tariffInformationAffordance)
                .generateDiscoveryLink(indicators, controller.getClass(), null);
    }
}
