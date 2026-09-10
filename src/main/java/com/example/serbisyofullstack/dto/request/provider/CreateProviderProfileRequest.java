package com.example.serbisyofullstack.dto.request.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for the authenticated user to create their provider profile
 * ({@code ProviderProfile}). Verification status, ratings, and documents are
 * managed elsewhere — not through this payload.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProviderProfileRequest {
    @Schema(description = "Registered or trading name of the service business.")
    @NotBlank(message = "Business name is required")
    @Size(max = 150, message = "Business name must not exceed 150 characters")
    private String businessName;

    @Schema(description = "Public description of the services offered, experience, etc.")
    @Size(max = 2000,  message = "Bio must not exceed 2000 characters")
    private String bio;
}
