package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.review.CreateReviewRequest;
import com.example.serbisyofullstack.dto.nested.ReviewSummaryDto;
import com.example.serbisyofullstack.dto.response.review.CreateReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Reviews: only COMPLETED bookings can be reviewed, once, by the booking's own
 * customer.
 */
public interface ReviewService {

    CreateReviewResponse createReview(Long customerUserId, CreateReviewRequest request);

    ReviewSummaryDto getReview(Long reviewId);

    Page<ReviewSummaryDto> listReviewsForProvider(Long providerId, Pageable pageable);

    Page<ReviewSummaryDto> listReviewsForCustomer(Long customerUserId, Pageable pageable);
}
