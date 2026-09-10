package com.example.serbisyofullstack.dto.request.address;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Request body for partially updating an existing {@code Address}.
 * All fields are optional; only the supplied ones overwrite stored values.
 * Ownership is enforced by the service layer, not by this payload.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateAddressRequest {
    @Schema(description = "Friendly name for the address, e.g. \"Home\" or \"Office\".")
    @Size(max = 50)
    private String label;

    @Schema(description = "Street-level details: house number, street, subdivision, etc.")
    @Size(max = 255)
    private String addressLine;

    @Schema(description = "Barangay, village, or district.")
    @Size(max = 100)
    private String locality;

    @Schema(description = "City or municipality.")
    @Size(max = 100)
    private String city;

    @Schema(description = "Province or region.")
    @Size(max = 100)
    private String region;

    @Schema(description = "ZIP / postal code.")
    @Size(max = 20)
    private String postalCode;

    @Schema(description = "Country name or ISO code.")
    @Size(max = 100)
    private String country;

    @Schema(description = "Geographic latitude; must be within valid ranges when supplied.")
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @Schema(description = "Geographic longitude; must be within valid ranges when supplied.")
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;
}
