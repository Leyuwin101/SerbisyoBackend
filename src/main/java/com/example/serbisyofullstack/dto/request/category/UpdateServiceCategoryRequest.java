package com.example.serbisyofullstack.dto.request.category;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin request body for partially updating a service category. All fields
 * optional; only the supplied ones are applied.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateServiceCategoryRequest {
    @Schema(description = "Updated unique category name.")
    @Size(max = 100)
    private String name;

    @Schema(description = "Updated description of the category.")
    @Size(max = 100)
    private String description;
}
