package com.example.serbisyofullstack.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for changing the authenticated user's password.
 * The server verifies {@code currentPassword} against the stored hash and
 * that the two new values match before updating
 * {@link com.example.serbisyofullstack.model.entity.User}.
 */
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequest {
    @Schema(description = "The account's current password, required as proof of identity.")
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @Schema(description = "Replacement password; must be 8-100 characters.")
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
    private String newPassword;

    @Schema(description = "Must exactly match {@code newPassword}.")
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
