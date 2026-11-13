package energy.eddie.implicitflexibility.datasource.es.esios.query;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.OffsetDateTime;

public class EsiosTariffQueryValidator
        implements ConstraintValidator<ValidEsiosTariffQuery, EsiosTariffQuery> {

    @Override
    public boolean isValid(
            @Nullable EsiosTariffQuery query,
            ConstraintValidatorContext context
    ) {
        if (query == null) {
            return true;
        }

        OffsetDateTime datetime = query.getDatetime();
        OffsetDateTime startDate = query.getStartDate();
        OffsetDateTime endDate = query.getEndDate();

        boolean hasDatetime = datetime != null;
        boolean hasStartDate = startDate != null;
        boolean hasEndDate = endDate != null;

        if (hasDatetime && (hasStartDate || hasEndDate)) {
            return buildViolation(
                    context,
                    "datetime cannot be combined with startDate or endDate."
            );
        }

        if (hasStartDate != hasEndDate) {
            return buildViolation(
                    context,
                    "startDate and endDate must be provided together."
            );
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return buildViolation(
                    context,
                    "startDate must be before or equal to endDate."
            );
        }

        return true;
    }

    private boolean buildViolation(
            ConstraintValidatorContext context,
            String message
    ) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
               .addConstraintViolation();

        return false;
    }
}
