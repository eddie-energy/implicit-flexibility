package energy.eddie.implicitflexibility.transport.hateoas;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;

public final class HalFormsEnumOptions {

    private HalFormsEnumOptions() { }

    public static HalFormsConfiguration register(HalFormsConfiguration configuration, Class<?> formType) {
        if (formType.isRecord()) {
            return registerRecord(configuration, formType);
        }
        return registerBean(configuration, formType);
    }

    private static HalFormsConfiguration registerRecord(HalFormsConfiguration configuration, Class<?> formType) {
        for (RecordComponent component : formType.getRecordComponents()) {
            Class<?> propertyType = component.getType();
            if (!propertyType.isEnum()) {
                continue;
            }
            configuration = registerEnum(configuration, formType, component.getName(), propertyType);
        }
        return configuration;
    }

    private static HalFormsConfiguration registerBean(HalFormsConfiguration configuration, Class<?> formType) {

        try {
            for (PropertyDescriptor property : Introspector.getBeanInfo(formType, Object.class)
                            .getPropertyDescriptors()) {

                Method getter = property.getReadMethod();
                if (getter == null) {
                    continue;
                }

                Class<?> propertyType = getter.getReturnType();
                if (!propertyType.isEnum()) {
                    continue;
                }

                configuration = registerEnum(configuration, formType, property.getName(), propertyType);
            }

            return configuration;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to inspect HAL-FORMS type: " + formType.getName(), e);
        }
    }

    private static HalFormsConfiguration registerEnum(HalFormsConfiguration configuration,
                                                      Class<?> formType,
                                                      String propertyName,
                                                      Class<?> enumType) {
        return configuration.withOptions(formType, propertyName,
                metadata -> HalFormsOptions.inline(enumValues(enumType)));
    }

    private static List<String> enumValues(Class<?> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
                .map(HalFormsEnumOptions::serializedValue)
                .toList();
    }

    private static String serializedValue(Object enumValue) {
        try {
            Method getValue = enumValue.getClass().getMethod("getValue");
            return String.valueOf(getValue.invoke(enumValue));
        } catch (NoSuchMethodException e) {
            return ((Enum<?>) enumValue).name();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not determine serialized value for enum: "
                            + enumValue.getClass().getName(), e);
        }
    }
}