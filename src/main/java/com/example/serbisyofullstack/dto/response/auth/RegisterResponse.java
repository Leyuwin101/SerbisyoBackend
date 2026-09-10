package com.example.serbisyofullstack.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.dto.nested.UserSummaryDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for a successful registration. Returns the newly created
 * account (as a {@link UserSummaryDto}) plus the token pair so the client is
 * logged in immediately after signing up.
 */
@Getter
@Setter
@NoArgsConstructor
public class RegisterResponse {

    /**
     * Short-lived JWT access token for the Authorization header.
     */
    @NotBlank
    private String accessToken;

    /**
     * Opaque refresh token used with the refresh endpoint.
     */
    @NotBlank
    private String refreshToken;

    @Schema(description = "The created user, without sensitive fields (password, etc.).")
    private UserSummaryDto user;
}
