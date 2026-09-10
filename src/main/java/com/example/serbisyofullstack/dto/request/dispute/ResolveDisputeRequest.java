package com.example.serbisyofullstack.dto.request.dispute;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin request body for closing a dispute. {@code resolvedBy} and
 * {@code resolvedAt} on the {@code Dispute} are stamped from the current
 * admin user; any refund handling is triggered separately.
 */
@Getter
@Setter
@NoArgsConstructor
public class ResolveDisputeRequest {
    @Schema(description = "The final decision and explanation, e.g. \"Refund issued to customer\".")
    @NotBlank(message = "Resolution is required")
    @Size(max = 3000, message = "Resolution must not exceed 3000 characters")
    private String resolution;
}
