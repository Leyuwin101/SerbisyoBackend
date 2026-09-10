package com.example.serbisyofullstack.dto.request.service;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A single image attached to a service. Nested inside
 * {@link CreateServiceRequest}; {@code displayOrder} controls the gallery
 * ordering on the service page.
 */
@Getter
@Setter
@NoArgsConstructor
public class ServiceImageRequest {
    @Schema(description = "Public URL of the uploaded image.")
    @NotBlank(message = "Image URL is required")
    @Size(max = 500)
    private String imageUrl;

    @Schema(description = "Zero-based position in the gallery; lower shows first.")
    @Min(value = 0, message = "Display order cannot be negative")
    private Integer displayOrder;
}
