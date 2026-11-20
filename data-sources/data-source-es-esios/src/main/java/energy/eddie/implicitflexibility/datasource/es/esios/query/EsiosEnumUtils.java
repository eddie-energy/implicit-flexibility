package energy.eddie.implicitflexibility.datasource.es.esios.query;

import java.util.Arrays;

public final class EsiosEnumUtils {

    private EsiosEnumUtils() {}

    public static <T extends Enum<T> & EsiosEnum> T fromValue(
            Class<T> enumClass,
            String value
    ) {
        return Arrays.stream(enumClass.getEnumConstants())
                     .filter(item -> item.getValue().equalsIgnoreCase(value))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                             "Unknown value '%s' for %s".formatted(
                                     value, enumClass.getSimpleName()
                             )
                     ));
    }
}
