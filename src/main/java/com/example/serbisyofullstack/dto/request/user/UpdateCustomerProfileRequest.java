package com.example.serbisyofullstack.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for updating the authenticated customer's profile
 * ({@link com.example.serbisyofullstack.model.entity.CustomerProfile}).
 * All fields are optional; only the supplied ones are applied.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateCustomerProfileRequest {
    @Schema(description = "Public name shown on reviews, bookings, and messages.")
    @Size(max = 100, message = "Display name must not exceed 100 characters")
    private String displayName;

    @Schema(description = "URL of the profile avatar image.")
    @Size(max = 500, message = "Avatar URL must not exceed 500 character")
    private String avatarUrl;

    @Schema(description = "ID of one of the customer's own {@code Address} records to use as the default.")
    private Long defaultAddressId;
}
