package com.example.serbisyofullstack.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for the login endpoint. On success the server issues an
 * access token (JWT) and a new refresh token.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    @Schema(description = "Email of the account trying to authenticate.")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(description = "Plaintext password; compared against the stored hash.")
    @NotBlank(message = "Password is required")
    private String password;
}
