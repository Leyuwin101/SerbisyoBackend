package com.example.serbisyofullstack.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Field-level constraint: value must be one of a fixed set (case-insensitive).
 * Useful for free-text fields with an implicit allowed vocabulary.
 */
@Documented
@Constraint(validatedBy = OneOfValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface OneOf {

    String message() default "Value must be one of: {value}";

    String[] value();

    boolean ignoreCase() default true;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
