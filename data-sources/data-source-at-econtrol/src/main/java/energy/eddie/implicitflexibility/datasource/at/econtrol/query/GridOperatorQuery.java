package energy.eddie.implicitflexibility.datasource.at.econtrol.query;

import energy.eddie.datasource.at.econtrol.EnergyType;

public record GridOperatorQuery(String zipCode, EnergyType energyType) { }
