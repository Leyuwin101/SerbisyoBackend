package com.example.serbisyofullstack.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for refreshing tokens. Rotates both tokens: the old refresh
 * token is revoked ({@code RefreshToken.revokedAt}) and a new pair issued.
 */
@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenResponse {

    @Schema(description = "New short-lived JWT access token.")
    private String accessToken;

    @Schema(description = "New refresh token; the previous one is no longer valid.")
    private String refreshToken;
}
