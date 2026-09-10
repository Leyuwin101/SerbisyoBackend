package com.example.serbisyofullstack.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

/**
 * Validates {@link OneOf} against a fixed, case-insensitive set. Nulls are
 * ignored — pair with {@code @NotBlank} when required.
 */
public class OneOfValidator implements ConstraintValidator<OneOf, CharSequence> {

    private String[] allowed;
    private boolean ignoreCase;

    @Override
    public void initialize(OneOf annotation) {
        this.allowed = annotation.value();
        this.ignoreCase = annotation.ignoreCase();
    }

    @Override
    public boolean isValid(CharSequence input, ConstraintValidatorContext context) {
        if (input == null) {
            return true;
        }
        String candidate = input.toString();
        return Arrays.stream(allowed).anyMatch(option
                -> ignoreCase ? option.equalsIgnoreCase(candidate) : option.equals(candidate));
    }
}
