package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ReviewSummaryDto;
import com.example.serbisyofullstack.dto.request.review.CreateReviewRequest;
import com.example.serbisyofullstack.model.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Review} and {@link ReviewSummaryDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    @Mapping(source = "reviewId", target = "id")
    @Mapping(source = "booking.bookingId", target = "bookingId")
    @Mapping(source = "customer.customerProfileId", target = "customerId")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    ReviewSummaryDto toDto(Review entity);

    @Mapping(target = "reviewId", ignore = true)
    @Mapping(target = "booking", ignore = true)    // resolved by the service
    @Mapping(target = "customer", ignore = true)   // from the authenticated principal
    @Mapping(target = "provider", ignore = true)   // derived from the booking
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(CreateReviewRequest request);
}
