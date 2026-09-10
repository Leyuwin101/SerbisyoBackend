package com.example.serbisyofullstack.dto.response.dispute;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.DisputeDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after an admin resolves a dispute. Returns the dispute with its
 * RESOLVED status, the resolution text, and who resolved it.
 */
@Getter
@Setter
@NoArgsConstructor
public class ResolveDisputeResponse {

    @Schema(description = "The dispute after resolution, including resolvedBy/resolvedAt.")
    private DisputeDto dispute;

    @Schema(description = "Server timestamp of the resolution.")
    private LocalDateTime resolvedAt;
}
