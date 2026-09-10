package com.example.serbisyofullstack.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.temporal.Temporal;

/**
 * Validates {@link MaxDuration}: the span between the start and end fields must
 * not exceed the configured hours. Supports any java.time Temporal that
 * Duration can span (LocalDateTime, OffsetDateTime, Instant, ...). Nulls are
 * ignored — pair with {@code @NotNull} on the fields.
 */
@Slf4j
public class DurationValidator implements ConstraintValidator<MaxDuration, Object> {

    private String startField;
    private String endField;
    private long maxHours;

    @Override
    public void initialize(MaxDuration annotation) {
        this.startField = annotation.startField();
        this.endField = annotation.endField();
        this.maxHours = annotation.maxHours();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        Temporal start = read(value, startField);
        Temporal end = read(value, endField);
        if (start == null || end == null) {
            return true;
        }

        try {
            Duration duration = Duration.between(start, end);
            boolean valid = !duration.isNegative() && duration.toHours() <= maxHours;
            if (!valid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(endField)
                        .addConstraintViolation();
            }
            return valid;
        } catch (Exception ex) {
            log.warn("Could not compute duration between '{}' and '{}': {}",
                    startField, endField, ex.getMessage());
            return true; // don't fail on incompatible types; leave that to other constraints
        }
    }

    @SuppressWarnings("unchecked")
    private Temporal read(Object bean, String fieldName) {
        try {
            Field field = bean.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object result = field.get(bean);
            return result instanceof Temporal t ? t : null;
        } catch (ReflectiveOperationException ex) {
            log.error("Cannot read field '{}' on {}: {}", fieldName, bean.getClass().getSimpleName(), ex.getMessage());
            return null;
        }
    }
}
