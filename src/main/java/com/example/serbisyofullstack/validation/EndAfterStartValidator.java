package com.example.serbisyofullstack.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.temporal.Temporal;

/**
 * Validates {@link EndAfterStart}: the end value must be strictly after the
 * start value. Nulls are ignored — pair with {@code @NotNull} on the fields.
 */
@Slf4j
public class EndAfterStartValidator implements ConstraintValidator<EndAfterStart, Object> {

    private String startField;
    private String endField;

    @Override
    public void initialize(EndAfterStart annotation) {
        this.startField = annotation.startField();
        this.endField = annotation.endField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Comparable<Object> start = read(value, startField);
        Comparable<Object> end = read(value, endField);
        if (start == null || end == null) {
            return true;
        }

        boolean valid = end.compareTo((Object) start) > 0;
        if (!valid) {
            // Report the violation on the end field rather than the class level.
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(endField)
                    .addConstraintViolation();
        }
        return valid;
    }

    @SuppressWarnings("unchecked")
    private Comparable<Object> read(Object bean, String fieldName) {
        try {
            Field field = bean.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object result = field.get(bean);
            if (result instanceof Temporal || result instanceof Comparable<?>) {
                return (Comparable<Object>) result;
            }
            log.warn("Field '{}' is not Comparable; skipping EndAfterStart validation", fieldName);
            return null;
        } catch (ReflectiveOperationException ex) {
            log.error("Cannot read field '{}' on {}: {}", fieldName, bean.getClass().getSimpleName(), ex.getMessage());
            return null;
        }
    }
}
