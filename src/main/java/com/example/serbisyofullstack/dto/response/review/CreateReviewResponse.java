package com.example.serbisyofullstack.dto.response.review;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.ReviewSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for creating a review. The nested {@link ReviewSummaryDto}
 * carries the saved review; the provider's recomputed average rating is
 * returned alongside so the client can update the UI immediately.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateReviewResponse {

    @Schema(description = "The saved review, including its server-generated id.")
    private ReviewSummaryDto review;

    @Schema(description = "The provider's average rating after including this review.")
    private Double providerAverageRating;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
