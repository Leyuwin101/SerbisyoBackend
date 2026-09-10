package com.example.serbisyofullstack.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.dto.nested.UserSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for a successful login: the token pair plus the authenticated
 * user's summary so the client can hydrate its session.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    @Schema(description = "Short-lived JWT access token for the Authorization header.")
    private String accessToken;

    @Schema(description = "Opaque refresh token used with the refresh endpoint.")
    private String refreshToken;

    @Schema(description = "Basic info of the authenticated user (id, email, phone, status).")
    private UserSummaryDto user;
}
