package com.example.serbisyofullstack.dto.request.address;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request body for creating a new {@code Address} owned by the
 * authenticated user. Coordinates allow map pinning and service-area checks.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateAddressRequest {
    @Schema(description = "Friendly name for the address, e.g. \"Home\" or \"Office\".")
    @NotBlank(message = "Address label is required")
    @Size(max = 50, message = "Label must not exceed 50 characters")
    private String label;

    @Schema(description = "Street-level details: house number, street, subdivision, etc.")
    @NotBlank(message = "Address line is required")
    @Size(max = 255, message = "Address line must not exceed 255 characters")
    private String addressLine;

    @Schema(description = "Barangay, village, or district.")
    @Size(max = 100, message = "Locality must not exceed 100 characters")
    private String locality;

    @Schema(description = "City or municipality.")
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Schema(description = "Province or region.")
    @NotBlank(message = "Region is required")
    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region;

    @Schema(description = "ZIP / postal code.")
    @NotBlank(message = "Postal code is required")
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;

    @Schema(description = "Country name or ISO code.")
    @NotBlank(message = "Country is required")
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Schema(description = "Geographic latitude; required so bookings can be matched to providers.")
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private BigDecimal latitude;

    @Schema(description = "Geographic longitude.")
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private BigDecimal longitude;
}
