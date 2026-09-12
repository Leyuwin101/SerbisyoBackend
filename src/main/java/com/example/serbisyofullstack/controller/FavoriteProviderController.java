package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.FavoriteProviderDto;
import com.example.serbisyofullstack.dto.request.favorite.FavoriteProviderRequest;
import com.example.serbisyofullstack.dto.response.favorite.FavoriteProviderResponse;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.FavoriteProviderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Customer favorites (saved providers). The owner is always the authenticated
 * principal.
 */
@Tag(name = "Favorites", description = "Providers saved by the authenticated customer")
@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteProviderController {

    private final FavoriteProviderService favoriteProviderService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Save a provider as a favorite")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Provider favorited"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Provider not found"),
        @ApiResponse(responseCode = "409", description = "Provider already favorited")
    })
    @PostMapping
    public ResponseEntity<FavoriteProviderResponse> addFavorite(
            @Valid @RequestBody FavoriteProviderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(favoriteProviderService.addFavorite(
                        currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "List the user's favorite providers")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Favorites retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<Page<FavoriteProviderDto>> listFavorites(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(favoriteProviderService.listFavorites(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Remove a provider from favorites")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Favorite removed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Favorite not found")
    })
    @DeleteMapping("/{providerId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long providerId) {
        favoriteProviderService.removeFavorite(currentUserService.getCurrentUserId(), providerId);
        return ResponseEntity.noContent().build();
    }
}
