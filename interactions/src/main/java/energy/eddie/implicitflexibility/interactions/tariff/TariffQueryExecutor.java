package energy.eddie.implicitflexibility.interactions.tariff;

public interface TariffQueryExecutor {
    String getCountryCode();

    Class<?> getQueryType();

    Object execute(Object query);
}
