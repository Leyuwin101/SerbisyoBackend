package com.example.serbisyofullstack.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Cross-field constraint: a range defined by a lower and an upper field must
 * span at most {@code maxHours} (e.g. a booking slot must not exceed 12h).
 */
@Documented
@Constraint(validatedBy = DurationValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MaxDuration {

    String message() default "Duration must not exceed {maxHours} hours";

    String startField();

    String endField();

    long maxHours();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
