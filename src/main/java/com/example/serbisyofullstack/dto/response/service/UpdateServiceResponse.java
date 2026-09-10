package com.example.serbisyofullstack.dto.response.service;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

import com.example.serbisyofullstack.dto.nested.ServiceImageDto;
import com.example.serbisyofullstack.dto.nested.ServiceSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for partially updating a service. Returns the full updated
 * record plus the current gallery images.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateServiceResponse {

    @Schema(description = "The service after applying the update.")
    private ServiceSummaryDto service;

    @Schema(description = "Current gallery images in display order.")
    private List<ServiceImageDto> images;

    @Schema(description = "Server timestamp of the update.")
    private LocalDateTime updatedAt;
}
