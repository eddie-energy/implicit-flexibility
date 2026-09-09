package energy.eddie.implicitflexibility.datasource.at.econtrol.query;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import energy.eddie.datasource.at.econtrol.EnergyType;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "energyType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PowerProductQuery.class, name = "POWER"),
        @JsonSubTypes.Type(value = GasProductQuery.class, name = "GAS")
})
public interface ProductQuery {
    long getBrandId();
    boolean getSmartMeter();
    EnergyType getEnergyType();
}
