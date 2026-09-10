package com.example.serbisyofullstack.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cross-field constraint: the end date/time must be after the start date/time.
 * Apply to a request class whose fields are named by {@link #startField()} and
 * {@link #endField()} and are Comparable (e.g. LocalDateTime, OffsetDateTime).
 * Repeatable so a class can validate multiple field pairs.
 */
@Documented
@Constraint(validatedBy = EndAfterStartValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(EndAfterStart.List.class)
public @interface EndAfterStart {

    String message() default "{field.end} must be after {field.start}";

    String startField();

    String endField();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Container enabling repeated {@link EndAfterStart} annotations on one class.
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface List {
        EndAfterStart[] value();
    }
}
