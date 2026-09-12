package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.dto.request.admin.UpdateUserRoleRequest;
import com.example.serbisyofullstack.dto.request.provider.ReviewProviderDocumentRequest;
import com.example.serbisyofullstack.dto.response.admin.UpdateUserRolesResponse;
import com.example.serbisyofullstack.dto.response.provider.ReviewProviderDocumentResponse;
import com.example.serbisyofullstack.model.enums.VerificationStatus;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.AdminService;
import com.example.serbisyofullstack.service.ProviderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Admin-only operations: role management, verification queue, document review.
 * The path is already guarded by SecurityConfig (ADMIN/MODERATOR); method
 * security re-asserts it. All actions are audited in the services.
 */
@Tag(name = "Admin", description = "Admin-only operations: users, roles, verification, documents")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final ProviderService providerService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Replace a user's roles")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Roles updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/users/roles")
    public ResponseEntity<UpdateUserRolesResponse> updateUserRoles(
            @Valid @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(adminService.updateUserRoles(
                currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "Set a provider's verification status")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Verification updated"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "404", description = "Provider not found")
    })
    @PostMapping("/providers/{providerId}/verification")
    public ResponseEntity<Void> setProviderVerification(
            @PathVariable Long providerId,
            @RequestParam VerificationStatus status,
            @RequestParam(required = false) String notes) {
        adminService.setProviderVerification(
                currentUserService.getCurrentUserId(), providerId, status, notes);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List users filtered by account status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator")
    })
    @GetMapping("/users")
    public ResponseEntity<Page<UserSummaryDto>> listUsersByStatus(
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.listUsersByStatus(
                status, PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Approve or reject a provider document")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Document reviewed"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "404", description = "Document not found")
    })
    @PostMapping("/documents/{documentId}/review")
    public ResponseEntity<ReviewProviderDocumentResponse> reviewProviderDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody ReviewProviderDocumentRequest request) {
        boolean approved = request.getStatus() == com.example.serbisyofullstack.model.enums.DocumentStatus.APPROVED;
        return ResponseEntity.ok(providerService.reviewProviderDocument(
                currentUserService.getCurrentUserId(), documentId, approved, null));
    }
}
