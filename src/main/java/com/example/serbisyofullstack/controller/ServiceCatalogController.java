package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.ServiceCategoryDto;
import com.example.serbisyofullstack.dto.nested.ServiceSummaryDto;
import com.example.serbisyofullstack.dto.request.category.CreateServiceCategoryRequest;
import com.example.serbisyofullstack.dto.request.category.UpdateServiceCategoryRequest;
import com.example.serbisyofullstack.dto.request.service.CreateServiceRequest;
import com.example.serbisyofullstack.dto.request.service.UpdateServiceRequest;
import com.example.serbisyofullstack.dto.response.category.CreateServiceCategoryResponse;
import com.example.serbisyofullstack.dto.response.category.UpdateServiceCategoryResponse;
import com.example.serbisyofullstack.dto.response.service.CreateServiceResponse;
import com.example.serbisyofullstack.dto.response.service.UpdateServiceResponse;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.ServiceCatalogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Service catalog endpoints. Category listing is public (browsing); category
 * mutation is admin-only; provider-owned service CRUD resolves the provider
 * from the authenticated principal.
 */
@Tag(name = "Service Catalog", description = "Public category/service browsing and provider-owned service CRUD")
@RestController
@RequiredArgsConstructor
public class ServiceCatalogController {

    private final ServiceCatalogService serviceCatalogService;
    private final CurrentUserService currentUserService;

    // ---------- categories ----------
    @Operation(summary = "List service categories (public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categories retrieved")
    })
    @GetMapping("/api/v1/categories")
    public ResponseEntity<Page<ServiceCategoryDto>> listCategories(
            @PageableDefault(size = 50) Pageable pageable) {
        return ResponseEntity.ok(serviceCatalogService.listCategories(PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Create a category (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Category created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "409", description = "Category name already exists")
    })
    @PostMapping("/api/v1/categories")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<CreateServiceCategoryResponse> createCategory(
            @Valid @RequestBody CreateServiceCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceCatalogService.createCategory(request));
    }

    @Operation(summary = "Update a category (admin/moderator)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Category updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin/moderator"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/api/v1/categories/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public ResponseEntity<UpdateServiceCategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceCategoryRequest request) {
        return ResponseEntity.ok(serviceCatalogService.updateCategory(id, request));
    }

    // ---------- services (provider-owned) ----------
    @Operation(summary = "Create a service (provider)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Service created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a provider"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PostMapping("/api/v1/services")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<CreateServiceResponse> createService(
            @Valid @RequestBody CreateServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(serviceCatalogService.createService(currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "Update a service (provider)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Service updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "403", description = "Service belongs to another provider"),
        @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PutMapping("/api/v1/services/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<UpdateServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody UpdateServiceRequest request) {
        return ResponseEntity.ok(serviceCatalogService.updateService(
                currentUserService.getCurrentUserId(), id, request));
    }

    // ---------- services (public browse/search) ----------
    @Operation(summary = "Browse all active services (public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Services retrieved")
    })
    @GetMapping("/api/v1/services")
    public ResponseEntity<Page<ServiceSummaryDto>> browseServices(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(serviceCatalogService.browseServices(PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Search services by title keyword (public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Results retrieved"),
        @ApiResponse(responseCode = "400", description = "Missing or empty q parameter")
    })
    @GetMapping("/api/v1/services/search")
    public ResponseEntity<Page<ServiceSummaryDto>> searchServices(
            @RequestParam("q") String q,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(serviceCatalogService.searchServices(q, PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Get a single service (public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Service retrieved"),
        @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @GetMapping("/api/v1/services/{id}")
    public ResponseEntity<ServiceSummaryDto> getService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceCatalogService.getService(id));
    }

    @Operation(summary = "List a category's services (public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Services retrieved"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/api/v1/categories/{id}/services")
    public ResponseEntity<Page<ServiceSummaryDto>> listServicesByCategory(
            @PathVariable Long id,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(serviceCatalogService.listServicesByCategory(id, PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "List the provider's own services")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Services retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a provider")
    })
    @GetMapping("/api/v1/providers/me/services")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Page<ServiceSummaryDto>> listOwnServices(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(serviceCatalogService.listOwnServices(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Activate a service (provider)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Service activated"),
        @ApiResponse(responseCode = "403", description = "Service belongs to another provider"),
        @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PostMapping("/api/v1/services/{id}/activate")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Void> activateService(@PathVariable Long id) {
        serviceCatalogService.activateService(currentUserService.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deactivate a service (provider)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Service deactivated"),
        @ApiResponse(responseCode = "403", description = "Service belongs to another provider"),
        @ApiResponse(responseCode = "404", description = "Service not found")
    })
    @PostMapping("/api/v1/services/{id}/deactivate")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Void> deactivateService(@PathVariable Long id) {
        serviceCatalogService.deactivateService(currentUserService.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a service (provider)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Service deleted"),
        @ApiResponse(responseCode = "403", description = "Service belongs to another provider"),
        @ApiResponse(responseCode = "404", description = "Service not found"),
        @ApiResponse(responseCode = "409", description = "Service has bookings and cannot be deleted")
    })
    @DeleteMapping("/api/v1/services/{id}")
    @PreAuthorize("hasRole('PROVIDER')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceCatalogService.deleteService(currentUserService.getCurrentUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
