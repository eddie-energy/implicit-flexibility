package energy.eddie.implicitflexibility.datasource.at.econtrol.tariff;

import energy.eddie.datasource.at.econtrol.*;
import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlClient;
import energy.eddie.implicitflexibility.datasource.at.econtrol.EControlDataSource;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GasProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.GridOperatorQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.PowerProductQuery;
import energy.eddie.implicitflexibility.datasource.at.econtrol.query.ProductQuery;
import energy.eddie.implicitflexibility.transport.tariff.TariffInformationAffordance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class EControlTariffControllerTest {

    private EControlClient client;
    private TariffInformationAffordance tariffInformationAffordance;
    private EControlTariffController controller;

    @BeforeEach
    void setUp() {
        client = mock(EControlClient.class);
        tariffInformationAffordance = mock(TariffInformationAffordance.class);
        controller = new EControlTariffController(client, tariffInformationAffordance);
    }

    @Test
    void testGridOperators() {
        // Given
        GridOperatorQuery query = mock(GridOperatorQuery.class);
        GridOperator gridOperator = mock(GridOperator.class);
        Link brandsLink = Link.of("/countries/AT/tariffs/discovery/brands", "brands");

        when(client.findGridOperators(query)).thenReturn(List.of(gridOperator));
        doReturn(brandsLink).when(tariffInformationAffordance).generateDiscoveryLink(
                "brands", controller.getClass(), BrandSearch.class);

        // When
        var result = controller.gridOperators(query);

        // Then
        assertEquals(1, result.getContent().size());
        EntityModel<GridOperator> model = result.getContent().iterator().next();

        assertSame(gridOperator, model.getContent());
        assertEquals(brandsLink, model.getLink("brands").orElseThrow());

        verify(client).findGridOperators(query);
        verify(tariffInformationAffordance).generateDiscoveryLink(
                "brands", controller.getClass(), BrandSearch.class);
    }

    @Test
    void testBrands_PowerProductsLink() {
        // Given
        BrandSearch query = mock(BrandSearch.class);
        BrandSearchResult brandSearchResult = mock(BrandSearchResult.class);

        when(query.getEnergyType()).thenReturn(EnergyType.POWER);
        when(client.findBrands(query)).thenReturn(brandSearchResult);

        Link productsLink = Link.of("/countries/AT/tariffs/discovery/products", "products");
        doReturn(productsLink)
                .when(tariffInformationAffordance)
                .generateDiscoveryLink("products", controller.getClass(), PowerProductQuery.class);

        // When
        EntityModel<BrandSearchResult> result = controller.brands(query);

        // Then
        assertSame(brandSearchResult, result.getContent());
        assertEquals(productsLink, result.getLink("products").orElseThrow());

        verify(client).findBrands(query);
        verify(tariffInformationAffordance).generateDiscoveryLink("products",
                                                                  controller.getClass(),
                                                                  PowerProductQuery.class);
    }

    @Test
    void testBrands_GasProductsLink() {
        // Given
        BrandSearch query = mock(BrandSearch.class);
        BrandSearchResult brandSearchResult = mock(BrandSearchResult.class);

        when(query.getEnergyType()).thenReturn(EnergyType.GAS);
        when(client.findBrands(query)).thenReturn(brandSearchResult);

        Link productsLink = Link.of("/countries/AT/tariffs/discovery/products", "products");
        doReturn(productsLink)
                .when(tariffInformationAffordance)
                .generateDiscoveryLink("products", controller.getClass(), GasProductQuery.class);

        // When
        EntityModel<BrandSearchResult> result = controller.brands(query);

        // Then
        assertSame(brandSearchResult, result.getContent());
        assertEquals(productsLink, result.getLink("products").orElseThrow());

        verify(client).findBrands(query);
        verify(tariffInformationAffordance)
                .generateDiscoveryLink("products", controller.getClass(), GasProductQuery.class);
    }

    @Test
    void testProducts() {
        // Given
        ProductQuery query = mock(ProductQuery.class);
        CurrentProductData productData = mock(CurrentProductData.class);

        when(client.findProducts(query)).thenReturn(productData);
        Link tariffInformationLink = Link.of("/countries/AT/tariffs", "tariff-information");

        doReturn(tariffInformationLink)
                .when(tariffInformationAffordance)
                .create(EControlDataSource.COUNTRY_CODE);

        // When
        EntityModel<CurrentProductData> result = controller.products(query);

        // Then
        assertSame(productData, result.getContent());
        assertEquals(tariffInformationLink, result.getLink("tariff-information").orElseThrow());

        verify(client).findProducts(query);
        verify(tariffInformationAffordance).create(EControlDataSource.COUNTRY_CODE);
    }

    @Test
    void testDiscovery() {
        // Given
        Link gridOperatorsLink = Link.of("/countries/AT/tariffs/discovery/grid-operators",
                                         "grid-operators");
        doReturn(gridOperatorsLink)
                .when(tariffInformationAffordance)
                .generateDiscoveryLink("grid-operators", controller.getClass(), GridOperatorQuery.class);

        // When
        RepresentationModel<?> result = controller.discovery();

        // Then
        assertEquals("/countries/AT/tariffs/discovery",
                     result.getRequiredLink("self").getHref());
        assertEquals(gridOperatorsLink, result.getRequiredLink("grid-operators"));
        verify(tariffInformationAffordance)
                .generateDiscoveryLink("grid-operators", controller.getClass(), GridOperatorQuery.class);
    }
}
