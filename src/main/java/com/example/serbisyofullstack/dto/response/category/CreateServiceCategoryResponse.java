package com.example.serbisyofullstack.dto.response.category;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ServiceCategoryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for creating a service category. Returns the stored category
 * (with generated id) as a {@link ServiceCategoryDto}.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateServiceCategoryResponse {

    @Schema(description = "The created category, including its server-generated id.")
    private ServiceCategoryDto category;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
