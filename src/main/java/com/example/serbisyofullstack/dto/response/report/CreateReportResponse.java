package com.example.serbisyofullstack.dto.response.report;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ReportSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for filing a report. The nested {@link ReportSummaryDto}
 * carries the report id and its initial status (OPEN), which the client can use
 * to track progress.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateReportResponse {

    @Schema(description = "The created report in summary form.")
    private ReportSummaryDto report;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
