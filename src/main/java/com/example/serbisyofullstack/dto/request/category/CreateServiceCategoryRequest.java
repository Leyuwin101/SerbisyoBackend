package com.example.serbisyofullstack.dto.request.category;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Admin request body for creating a new service category used to group
 * services in browsing and search.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateServiceCategoryRequest {
    @Schema(description = "Unique, customer-facing category name, e.g. \"Home Cleaning\".")
    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @Schema(description = "Short explanation of what belongs in this category.")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}
