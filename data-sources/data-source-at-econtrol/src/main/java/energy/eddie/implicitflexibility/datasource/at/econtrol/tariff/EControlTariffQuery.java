package energy.eddie.implicitflexibility.datasource.at.econtrol.tariff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EControlTariffQuery (@NotBlank String productId, @NotNull EControlInformationType informationType) { }
