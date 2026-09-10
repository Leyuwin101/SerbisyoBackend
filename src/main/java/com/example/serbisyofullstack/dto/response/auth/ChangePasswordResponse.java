package com.example.serbisyofullstack.dto.response.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for a successful password change. Carries a confirmation
 * message and the time of the change; the user may need to log in again
 * depending on session policy.
 */
@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordResponse {

    @Schema(description = "Human-readable confirmation, e.g. \"Password updated successfully\".")
    private String message;

    @Schema(description = "Server timestamp of when the password was changed.")
    private LocalDateTime changedAt;
}
