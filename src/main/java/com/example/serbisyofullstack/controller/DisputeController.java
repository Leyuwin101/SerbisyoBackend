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
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.DisputeDto;
import com.example.serbisyofullstack.dto.request.dispute.CreateDisputeRequest;
import com.example.serbisyofullstack.dto.request.dispute.ResolveDisputeRequest;
import com.example.serbisyofullstack.dto.response.dispute.CreateDisputeResponse;
import com.example.serbisyofullstack.dto.response.dispute.ResolveDisputeResponse;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.DisputeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Dispute endpoints. Users only see disputes they are party to; listing all
 * open disputes and resolving are admin/moderator operations.
 */
@Tag(name = "Disputes", description = "Booking disputes; resolution is admin/moderator only")
@RestController
@RequestMapping("/api/v1/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Open a dispute on a booking")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Dispute opened"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a party to the booking"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "409", description = "Booking already has an open dispute")
    })
    @PostMapping
    public ResponseEntity<CreateDisputeResponse> openDispute(
            @Valid @RequestBody CreateDisputeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(disputeService.openDispute(currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "List the user's disputes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Disputes retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<Page<DisputeDto>> listMine(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(disputeService.listDisputesForUser(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Get a dispute")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dispute retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a party to the dispute"),
        @ApiResponse(responseCode = "404", description = "Dispute not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DisputeDto> getDispute(@PathVariable Long id) {
        return ResponseEntity.ok(disputeService.getDispute(
                currentUserService.getCurrentUserId(), id));
    }

    @Operation(summary = "List all open disputes (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Open disputes retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator")
    })
    @GetMapping("/open")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<Page<DisputeDto>> listOpen(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(disputeService.listOpenDisputes(PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Resolve a dispute (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dispute resolved"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "404", description = "Dispute not found"),
        @ApiResponse(responseCode = "409", description = "Dispute already resolved")
    })
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<ResolveDisputeResponse> resolveDispute(
            @PathVariable Long id,
            @Valid @RequestBody ResolveDisputeRequest request) {
        return ResponseEntity.ok(disputeService.resolveDispute(
                currentUserService.getCurrentUserId(), id, request));
    }
}
