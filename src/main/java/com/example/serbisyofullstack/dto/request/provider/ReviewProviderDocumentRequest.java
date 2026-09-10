package com.example.serbisyofullstack.dto.request.provider;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.DocumentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin request body for approving or rejecting a submitted provider
 * document. {@code reviewedBy} and {@code reviewedAt} on the
 * {@code ProviderDocument} are stamped by the server from the current user.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReviewProviderDocumentRequest {
    @Schema(description = "The review decision: APPROVED, REJECTED, or PENDING.")
    @NotNull(message = "Document status is required")
    private DocumentStatus status;
}
