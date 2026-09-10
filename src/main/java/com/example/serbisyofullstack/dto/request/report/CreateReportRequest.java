package com.example.serbisyofullstack.dto.request.report;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for any authenticated user reporting inappropriate content
 * or behavior. {@code reportedBy} is taken from the authenticated principal;
 * routing to an admin ({@code assignedTo}) happens server-side.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateReportRequest {
    @Schema(description = "What kind of entity is being reported, e.g. \"USER\", \"SERVICE\", \"REVIEW\".")
    @NotBlank(message = "Entity type is required")
    private String entityType;

    @Schema(description = "Primary key of the reported entity.")
    @NotNull(message = "Entity id is required")
    private Long entityId;

    @Schema(description = "Free-text explanation of the problem.")
    @NotBlank(message = "Reason is required")
    @Size(max = 1000, message = "Reason must not exceed 1000 characters")
    private String reason;
}
