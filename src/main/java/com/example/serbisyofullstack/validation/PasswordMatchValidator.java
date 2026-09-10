package com.example.serbisyofullstack.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * Validates {@link PasswordMatch}: the confirmation field must equal the
 * password field. Nulls are ignored — pair with {@code @NotBlank} on fields.
 */
@Slf4j
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    private String passwordField;
    private String confirmField;

    @Override
    public void initialize(PasswordMatch annotation) {
        this.passwordField = annotation.passwordField();
        this.confirmField = annotation.confirmField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        String password = read(value, passwordField);
        String confirm = read(value, confirmField);
        if (password == null || confirm == null) {
            return true;
        }

        boolean valid = password.equals(confirm);
        if (!valid) {
            // Report the mismatch on the confirmation field.
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(confirmField)
                    .addConstraintViolation();
        }
        return valid;
    }

    private String read(Object bean, String fieldName) {
        try {
            Field field = bean.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object result = field.get(bean);
            return result instanceof String s ? s : null;
        } catch (ReflectiveOperationException ex) {
            log.error("Cannot read field '{}' on {}: {}", fieldName, bean.getClass().getSimpleName(), ex.getMessage());
            return null;
        }
    }
}
