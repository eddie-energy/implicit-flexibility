package energy.eddie.implicitflexibility.datasource.es.esios.query;

import jakarta.annotation.Nullable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

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

        var hasDatetime = query.getDatetime() != null;
        var startDate = query.getStartDate();
        var hasStartDate = startDate != null;
        var endDate = query.getEndDate();
        var hasEndDate = endDate != null;

        if (hasDatetime && (hasStartDate || hasEndDate)) {
            return buildViolation(
                    context,
                    "datetime cannot be combined with start date or end date."
            );
        }

        if (hasStartDate != hasEndDate) {
            return buildViolation(
                    context,
                    "start date and end date must be provided together."
            );
        }

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return buildViolation(
                    context,
                    "start date must be before or equal to end date."
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
