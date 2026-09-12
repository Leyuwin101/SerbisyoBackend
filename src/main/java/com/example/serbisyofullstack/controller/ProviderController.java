package com.example.serbisyofullstack.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.ProviderSummaryDto;
import com.example.serbisyofullstack.dto.request.provider.UpdateProviderProfileRequest;
import com.example.serbisyofullstack.dto.response.provider.UpdateProviderProfileResponse;
import com.example.serbisyofullstack.search.ProviderSearchCriteria;
import com.example.serbisyofullstack.search.ProviderSearchResult;
import com.example.serbisyofullstack.search.ProviderSearchService;
import com.example.serbisyofullstack.search.SearchSort;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.ProviderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Public provider browsing/search plus provider-owned profile management.
 * Search parameters are bound to typed DTO fields (no raw SQL fragments) and
 * everything list-shaped is paginated through {@link PaginationGuard}.
 */
@Tag(name = "Providers", description = "Public provider search and provider-owned profile management")
@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderSearchService providerSearchService;
    private final ProviderService providerService;
    private final CurrentUserService currentUserService;

    // ---------- public search ----------
    @Operation(summary = "Search providers with filters and sorting")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Search results retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid filter values")
    })
    @GetMapping("/search")
    public ResponseEntity<Page<ProviderSummaryDto>> searchProviders(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean verifiedOnly,
            @RequestParam(required = false) SearchSort sort,
            @PageableDefault(size = 20) Pageable pageable) {
        ProviderSearchCriteria criteria = new ProviderSearchCriteria(
                categoryId, serviceId, minPrice, maxPrice, latitude, longitude,
                radiusKm, minRating, verifiedOnly, null);
        Page<ProviderSearchResult> results = providerSearchService.search(
                criteria, PaginationGuard.cap(pageable), sort);
        Page<ProviderSummaryDto> dtos = results.map(this::toSummary);
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Get a provider's public profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Provider profile retrieved"),
        @ApiResponse(responseCode = "404", description = "Provider not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProviderSummaryDto> getProviderProfile(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.getProviderProfile(id));
    }

    // ---------- provider-owned ----------
    @Operation(summary = "Get the authenticated provider's own profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a provider")
    })
    @GetMapping("/me")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<ProviderSummaryDto> getOwnProfile() {
        return ResponseEntity.ok(providerService.getOwnProfile(currentUserService.getCurrentUserId()));
    }

    @Operation(summary = "Update the authenticated provider's own profile")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Profile updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a provider")
    })
    @PutMapping("/me")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<UpdateProviderProfileResponse> updateOwnProfile(
            @Valid @RequestBody UpdateProviderProfileRequest request) {
        return ResponseEntity.ok(providerService.updateProfile(
                currentUserService.getCurrentUserId(), request));
    }

    private ProviderSummaryDto toSummary(ProviderSearchResult r) {
        return ProviderSummaryDto.builder()
                .id(r.providerId())
                .userId(r.userId())
                .businessName(r.businessName())
                .bio(r.bio())
                .verificationStatus(r.verificationStatus())
                .averageRating(r.averageRating())
                .reviewCount(r.reviewCount())
                .build();
    }
}
