package com.example.serbisyofullstack.dto.request.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for requesting a refund of a paid booking. The refund amount
 * is derived from the payment by the service layer (full or policy-based
 * partial); only the justification comes from the client.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateRefundRequest {
    @Schema(description = "Why the refund is being requested; audited with the refund record.")
    @NotBlank(message = "Reason is required")
    @Size(max = 1000)
    private String reason;
}
