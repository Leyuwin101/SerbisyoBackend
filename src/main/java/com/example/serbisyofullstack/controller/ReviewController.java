package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.api.RateLimiter;
import com.example.serbisyofullstack.dto.nested.ReviewSummaryDto;
import com.example.serbisyofullstack.dto.request.review.CreateReviewRequest;
import com.example.serbisyofullstack.dto.response.review.CreateReviewResponse;
import com.example.serbisyofullstack.exception.TooManyRequestsException;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Review endpoints. Creating a review is rate limited; only the customer of a
 * COMPLETED booking can review (enforced in {@link ReviewService}).
 */
@Tag(name = "Reviews", description = "Customer reviews of completed bookings")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final CurrentUserService currentUserService;
    private final RateLimiter rateLimiter;

    @Operation(summary = "Review a completed booking")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Review created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not the booking's customer or booking is not completed"),
        @ApiResponse(responseCode = "404", description = "Booking not found"),
        @ApiResponse(responseCode = "409", description = "Booking already reviewed"),
        @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @PostMapping
    public ResponseEntity<CreateReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request) {
        Long userId = currentUserService.getCurrentUserId();
        if (!rateLimiter.tryAcquire("reviews:create:" + userId, 10, 60_000)) {
            throw new TooManyRequestsException("Too many review attempts, please try again later");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(userId, request));
    }

    @Operation(summary = "Get a single review")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Review retrieved"),
        @ApiResponse(responseCode = "404", description = "Review not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ReviewSummaryDto> getReview(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReview(id));
    }

    @Operation(summary = "List a provider's reviews (public)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reviews retrieved"),
        @ApiResponse(responseCode = "404", description = "Provider not found")
    })
    @GetMapping("/provider/{providerId}")
    public ResponseEntity<Page<ReviewSummaryDto>> listForProvider(
            @PathVariable Long providerId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.listReviewsForProvider(
                providerId, PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "List the authenticated customer's own reviews")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reviews retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<Page<ReviewSummaryDto>> listMine(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reviewService.listReviewsForCustomer(
                currentUserService.getCurrentUserId(), PaginationGuard.cap(pageable)));
    }
}
