package com.example.serbisyofullstack.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for exchanging a refresh token for a new access token.
 * The server validates the token against the stored hash in
 * {@link com.example.serbisyofullstack.model.entity.RefreshToken}
 * (not yet revoked, not expired) before issuing new tokens.
 */
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenRequest {
    @Schema(description = "The opaque refresh token previously returned by login or register.")
    @NotBlank(message = "Refresh token is required")
    private String token;
}
