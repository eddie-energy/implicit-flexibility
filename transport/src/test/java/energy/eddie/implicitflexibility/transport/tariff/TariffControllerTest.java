package energy.eddie.implicitflexibility.transport.tariff;

import energy.eddie.implicitflexibility.interactions.tariff.TariffInformationRepository;
import energy.eddie.implicitflexibility.interactions.tariff.TariffProviderRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TariffControllerTest {

    @Mock
    private TariffProviderRegistry tariffInteractionRegistry;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TariffInformationAffordance tariffInformationAffordance;

    @InjectMocks
    private TariffController controller;

    @Test
    void getTariffs_SelfLink() {
        // Given
        TariffInformationRepository interaction = mock(TariffInformationRepository.class);
        when(tariffInteractionRegistry.getTariffs("AT")).thenReturn(interaction);
        when(tariffInformationAffordance.create("AT")).thenReturn(Link.of("/countries/AT/tariffs/query"));

        // When
        RepresentationModel<?> result = controller.discover("AT");

        // Then
        assertEquals("/countries/AT/tariffs",
                     result.getLink(IanaLinkRelations.SELF).orElseThrow().getHref());
    }

    @Test
    void getTariffs_TariffInformationAffordance() {
        // Given
        TariffInformationRepository interaction = mock(TariffInformationRepository.class);
        Link affordance = Link.of("/countries/AT/tariffs/query");

        when(tariffInteractionRegistry.getTariffs("AT")).thenReturn(interaction);
        when(tariffInformationAffordance.create("AT")).thenReturn(affordance);

        // When
        RepresentationModel<?> result = controller.discover("AT");

        // Then
        assertTrue(result.getLinks().contains(affordance));
        verify(tariffInformationAffordance).create("AT");
    }

    @Test
    void getTariffs_TariffDiscovery() {
        // Given
        TariffInformationRepository interaction = mock(TariffInformationRepository.class,
                                                       withSettings().extraInterfaces(TariffDiscovery.class));
        TariffDiscovery discovery = (TariffDiscovery) interaction;
        Link discoveryLink = Link.of("/countries/AT/tariffs/discovery", "discovery");

        when(tariffInteractionRegistry.getTariffs("AT")).thenReturn(interaction);
        when(tariffInformationAffordance.create("AT")).thenReturn(Link.of("/countries/AT/tariffs/query"));
        when(discovery.getDiscoveryLink("AT")).thenReturn(discoveryLink);

        // When
        RepresentationModel<?> result = controller.discover("AT");

        // Then
        assertTrue(result.getLink("discovery").isPresent());
        assertEquals("/countries/AT/tariffs/discovery",
                     result.getLink("discovery").orElseThrow().getHref());
        verify(discovery).getDiscoveryLink("AT");
    }

    @Test
    void getTariffs_NoTariffDiscovery() {
        // Given
        TariffInformationRepository interaction = mock(TariffInformationRepository.class);

        when(tariffInteractionRegistry.getTariffs("AT")).thenReturn(interaction);
        when(tariffInformationAffordance.create("AT")).thenReturn(Link.of("/countries/AT/tariffs/query"));

        // When
        RepresentationModel<?> result = controller.discover("AT");

        // Then
        assertTrue(result.getLinks().stream().noneMatch(link -> link.getHref().contains("/discovery")));
    }

    @Test
    void executeTariffQuery() {
        // Given
        TariffInformationRepository interaction = mock(TariffInformationRepository.class);
        JsonNode request = mock(JsonNode.class);
        Object query = new Object();
        Object response = new Object();
        Class<?> queryType = Object.class;

        when(tariffInteractionRegistry.getTariffs("AT")).thenReturn(interaction);
        doReturn(queryType).when(interaction).getQueryType();
        doReturn(query).when(objectMapper).convertValue(request, queryType);
        when(interaction.execute(query)).thenReturn(response);

        // When
        Object result = controller.query("AT", request);

        // Then
        assertEquals(response, ((ResponseEntity<?>) result).getBody());
        verify(tariffInteractionRegistry).getTariffs("AT");
        verify(objectMapper).convertValue(request, queryType);
        verify(interaction).execute(query);
    }
}
