package energy.eddie.implicitflexibility.transport.hateoas;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsConfiguration;
import org.springframework.hateoas.mediatype.hal.forms.HalFormsOptions;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

public final class HalFormsEnumOptions {

    private HalFormsEnumOptions() {}

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

            String propertyName = getJsonPropertyName(
                    component.getName(),
                    component.getAccessor().getAnnotation(JsonProperty.class)
            );

            configuration = registerEnum(configuration, formType, propertyName, propertyType);
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

                String propertyName = getJsonPropertyName(
                        formType,
                        property,
                        getter
                );

                configuration = registerEnum(configuration, formType, propertyName, propertyType);
            }

            return configuration;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to inspect HAL-FORMS type: " + formType.getName(), e);
        }
    }

    private static HalFormsConfiguration registerEnum(
            HalFormsConfiguration configuration,
            Class<?> formType,
            String propertyName,
            Class<?> enumType
    ) {
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

    private static String getJsonPropertyName(
            Class<?> formType,
            PropertyDescriptor property,
            Method getter
    ) {
        JsonProperty annotation = getter.getAnnotation(JsonProperty.class);

        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }

        Method setter = property.getWriteMethod();

        if (setter != null) {
            annotation = setter.getAnnotation(JsonProperty.class);

            if (annotation != null && !annotation.value().isEmpty()) {
                return annotation.value();
            }
        }

        try {
            Field field = formType.getDeclaredField(property.getName());
            annotation = field.getAnnotation(JsonProperty.class);

            if (annotation != null && !annotation.value().isEmpty()) {
                return annotation.value();
            }
        } catch (NoSuchFieldException ignored) {
            // Fall back to the Java property name.
        }

        return property.getName();
    }

    private static String getJsonPropertyName(
            String propertyName,
            @Nullable JsonProperty annotation
    ) {
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }

        return propertyName;
    }
}