package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.ReviewSummaryDto;
import com.example.serbisyofullstack.dto.request.review.CreateReviewRequest;
import com.example.serbisyofullstack.dto.response.review.CreateReviewResponse;
import com.example.serbisyofullstack.exception.ConflictException;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.ReviewMapper;
import com.example.serbisyofullstack.model.entity.Booking;
import com.example.serbisyofullstack.model.entity.CustomerProfile;
import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.entity.Review;
import com.example.serbisyofullstack.model.enums.BookingStatus;
import com.example.serbisyofullstack.repository.BookingRepository;
import com.example.serbisyofullstack.repository.CustomerProfileRepository;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import com.example.serbisyofullstack.repository.ReviewRepository;
import com.example.serbisyofullstack.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reviews: only COMPLETED bookings can be reviewed, once, by the booking's own
 * customer. The provider's rating summary is recomputed here.
 */
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public CreateReviewResponse createReview(Long customerUserId, CreateReviewRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new ValidationException("Only completed bookings can be reviewed");
        }
        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new ConflictException("This booking has already been reviewed");
        }
        CustomerProfile customer = customerProfileRepository.findByUserId(customerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));
        if (!booking.getCustomer().getCustomerProfileId().equals(customer.getCustomerProfileId())) {
            throw new ForbiddenException("You can only review your own bookings");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setCustomer(customer);
        review.setProvider(booking.getProvider());
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);

        updateRatingSummary(booking.getProvider());

        CreateReviewResponse response = new CreateReviewResponse();
        response.setReview(reviewMapper.toDto(review));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryDto getReview(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(reviewMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewSummaryDto> listReviewsForProvider(Long providerId, Pageable pageable) {
        return reviewRepository.findByProviderId(providerId, pageable).map(reviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewSummaryDto> listReviewsForCustomer(Long customerUserId, Pageable pageable) {
        return reviewRepository.findByCustomerId(customerUserId, pageable).map(reviewMapper::toDto);
    }

    private void updateRatingSummary(ProviderProfile provider) {
        Double average = reviewRepository.findAverageRatingByProviderId(provider.getProviderProfileId());
        long count = reviewRepository.countByProviderId(provider.getProviderProfileId());
        provider.setAverageRating(average != null ? average : 0.0);
        provider.setReviewCount((int) count);
        providerProfileRepository.save(provider);
    }
}
