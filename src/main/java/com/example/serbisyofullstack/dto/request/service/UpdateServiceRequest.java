package com.example.serbisyofullstack.dto.request.service;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.PricingType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Provider request body for partially updating an existing service. All
 * fields are optional; only the supplied ones are applied. The provider is
 * derived from the authenticated principal, so it cannot be changed here.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateServiceRequest {

    @Schema(description = "Move the service to a different category.")
    private Long categoryId;

    @Schema(description = "Updated customer-facing title.")
    @Size(max = 150)
    private String name;

    @Schema(description = "Updated detailed description.")
    @Size(max = 3000)
    private String description;

    @Schema(description = "Updated pricing model, e.g. FIXED, HOURLY, PER_UNIT.")
    private PricingType pricingType;

    @Schema(description = "Updated base price or rate.")
    @DecimalMin("0.00")
    @Digits(integer = 12, fraction = 2)
    private BigDecimal basePrice;

    @Schema(description = "Updated expected duration in minutes.")
    @Min(1)
    @Max(1440)
    private Integer durationMinutes;

    @Schema(description = "Show/hide the service without deleting it.")
    private Boolean active;

}
