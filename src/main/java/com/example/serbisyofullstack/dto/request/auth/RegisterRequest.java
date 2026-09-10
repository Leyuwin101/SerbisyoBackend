package com.example.serbisyofullstack.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.RoleEnum;
import com.example.serbisyofullstack.validation.PasswordMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Request body for user registration.
 * Carries the credentials, contact info, and the roles the new account
 * should have (e.g. CUSTOMER and/or PROVIDER). The server validates that
 * {@code password} and {@code confirmPassword} match before creating the
 * {@link com.example.serbisyofullstack.model.entity.User}.
 */
@Getter
@Setter
@NoArgsConstructor
@PasswordMatch
public class RegisterRequest {
    @Schema(description = "Unique login email of the new account.")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email must not exceed 255 character")
    private String email;

    @Schema(description = "Plaintext password; must be 8-100 characters. Hashed before storage.")
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 amd 100 characters")
    private String password;

    @Schema(description = "Must exactly match {@code password}; rejected otherwise.")
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @Schema(description = "Contact number in international or local format (10-15 digits).")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    private String phone;

    @Schema(description = "Roles requested for the account. Ignored if the caller is not an admin.")
    private Set<RoleEnum> roles;
}
