package com.example.serbisyofullstack.dto.response.category;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ServiceCategoryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for partially updating a service category. Returns the full
 * updated record.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateServiceCategoryResponse {

    @Schema(description = "The category after applying the update.")
    private ServiceCategoryDto category;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
