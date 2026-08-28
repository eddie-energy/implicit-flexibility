package energy.eddie.implicitflexibility.interactions.tariff;

public interface TariffInformationRepository{
    String getCountryCode();
    Class<?> getQueryType();
    Object execute(Object query);
}
