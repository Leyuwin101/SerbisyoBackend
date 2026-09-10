package com.example.serbisyofullstack.dto.request.review;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body for a customer reviewing a completed booking. The provider
 * and customer sides of the review are derived from the booking, and the
 * provider's {@code averageRating}/{@code reviewCount} are recomputed by
 * the server.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateReviewRequest {
    @Schema(description = "The completed booking being reviewed; one review per booking.")
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @Schema(description = "Star rating from 1 (worst) to 5 (best).")
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @Schema(description = "Optional written feedback about the service.")
    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;
}
