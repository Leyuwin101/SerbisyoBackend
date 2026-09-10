package com.example.serbisyofullstack.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for a user (or admin) updating core account details of a
 * {@link com.example.serbisyofullstack.model.entity.User}. All fields are
 * optional; only the supplied ones are applied.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {
    @Schema(description = "New email address; must remain unique across accounts.")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    @Schema(description = "New contact number in international or local format (10-15 digits).")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;
}
