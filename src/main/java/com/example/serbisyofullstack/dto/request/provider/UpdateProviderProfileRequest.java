package com.example.serbisyofullstack.dto.request.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for partially updating the authenticated provider's own
 * profile. All fields are optional; only the supplied ones are applied.
 * Verification status and ratings cannot be changed here.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateProviderProfileRequest {
    @Schema(description = "Updated trading name of the service business.")
    @Size(max = 150, message = "Business name must not exceed 150")
    private String businessName;

    @Schema(description = "Updated public description of the services offered.")
    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;
}
