package energy.eddie.implicitflexibility.datasource.es.esios.query;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EsiosTariffQueryValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEsiosTariffQuery {

    String message() default "Invalid ESIOS tariff query.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
