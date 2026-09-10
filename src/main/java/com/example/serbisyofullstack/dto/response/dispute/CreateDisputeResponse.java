package com.example.serbisyofullstack.dto.response.dispute;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.DisputeDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for opening a dispute. The nested {@link DisputeDto} carries
 * the dispute id, its OPEN status, and the booking it targets.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateDisputeResponse {

    @Schema(description = "The created dispute in full detail.")
    private DisputeDto dispute;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
