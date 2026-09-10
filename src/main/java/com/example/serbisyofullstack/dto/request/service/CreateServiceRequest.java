package com.example.serbisyofullstack.dto.request.service;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.PricingType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Provider request body for creating a new service offering. Pricing depends
 * on {@code pricingType}: FIXED uses {@code basePrice} as the total, while
 * hourly/per-unit types use it as the rate. Ratings are server-computed.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateServiceRequest {

    @Schema(description = "The category this service belongs to, e.g. \"Home Cleaning\".")
    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @Schema(description = "Customer-facing service title.")
    @NotBlank(message = "Service name is required")
    @Size(max = 150, message = "Service name must not exceed 150 characters")
    private String name;

    @Schema(description = "Detailed description of what is included in the service.")
    @Size(max = 3000, message = "Description must not exceed 3000 characters")
    private String description;

    @Schema(description = "How the service is priced, e.g. FIXED, HOURLY, PER_UNIT.")
    @NotNull(message = "Pricing type is required")
    private PricingType pricingType;

    @Schema(description = "Base price (total for FIXED, rate otherwise).")
    @DecimalMin(value = "0.00", inclusive = true, message = "Base price cannot be negative")
    @Digits(integer = 12, fraction = 2, message = "Invalid price")
    private BigDecimal basePrice;

    @Schema(description = "Expected duration of the job in minutes (up to one day).")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 1440, message = "Duration cannot exceed 1440 minutes")
    private Integer durationMinutes;

    @Schema(description = "Whether the service is bookable immediately after creation.")
    private boolean active = true;

    @Schema(description = "Optional gallery images; validated with {@link ServiceImageRequest}.")
    @Valid
    private List<ServiceImageRequest> images;
}
