package energy.eddie.implicitflexibility.datasource.at.econtrol.tariff;

import jakarta.validation.constraints.NotNull;

public class EControlTariffQuery {
    @NotNull
    private String productId;

    @NotNull
    private EControlInformationType informationType;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public EControlInformationType getInformationType() {
        return informationType;
    }

    public void setInformationType(EControlInformationType informationType) {
        this.informationType = informationType;
    }
}
