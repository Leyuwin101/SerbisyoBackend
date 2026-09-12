package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.api.RateLimiter;
import com.example.serbisyofullstack.dto.nested.ReportSummaryDto;
import com.example.serbisyofullstack.dto.request.report.CreateReportRequest;
import com.example.serbisyofullstack.dto.response.report.CreateReportResponse;
import com.example.serbisyofullstack.exception.TooManyRequestsException;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Report endpoints. Creating reports is rate limited; listing all reports and
 * resolving them is admin/moderator work.
 */
@Tag(name = "Reports", description = "User-submitted reports; triage is admin/moderator only")
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final CurrentUserService currentUserService;
    private final RateLimiter rateLimiter;

    @Operation(summary = "Submit a report")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Report submitted"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "429", description = "Too many reports submitted")
    })
    @PostMapping
    public ResponseEntity<CreateReportResponse> createReport(
            @Valid @RequestBody CreateReportRequest request,
            HttpServletRequest httpRequest) {
        Long userId = currentUserService.getCurrentUserId();
        if (!rateLimiter.tryAcquire("reports:create:" + userId, 5, 60_000)) {
            throw new TooManyRequestsException("Too many reports submitted, please try again later");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reportService.createReport(userId, request));
    }

    @Operation(summary = "List the caller's own reports")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reports retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<Page<ReportSummaryDto>> listMine(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reportService.listOwnReports(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "List all reports (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reports retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator")
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<Page<ReportSummaryDto>> listAll(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reportService.listReports(PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Get a report")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Report retrieved"),
        @ApiResponse(responseCode = "404", description = "Report not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReportSummaryDto> getReport(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReport(id));
    }

    @Operation(summary = "Resolve a report (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Report resolved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "404", description = "Report not found"),
        @ApiResponse(responseCode = "409", description = "Report already resolved")
    })
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<Void> resolveReport(
            @PathVariable Long id,
            @RequestParam boolean upheld,
            @RequestParam(required = false) String resolution) {
        reportService.resolveReport(currentUserService.getCurrentUserId(), id, upheld, resolution);
        return ResponseEntity.noContent().build();
    }
}
