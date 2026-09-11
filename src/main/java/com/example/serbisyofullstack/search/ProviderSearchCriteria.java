package com.example.serbisyofullstack.search;

import java.math.BigDecimal;

/**
 * Filter/sort criteria for provider discovery. Validated server-side — invalid
 * ranges (bad lat/lng, inverted price range, out-of-range rating) are rejected
 * before the query runs.
 */
public record ProviderSearchCriteria(
        Long categoryId,
        Long serviceId,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        Double latitude,
        Double longitude,
        Double radiusKm,
        Double minimumRating,
        Boolean verifiedOnly,
        Boolean availableOnly
        ) {

    public ProviderSearchCriteria {
        if (latitude != null && (latitude < -90.0 || latitude > 90.0)) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (longitude != null && (longitude < -180.0 || longitude > 180.0)) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        if (radiusKm != null && (radiusKm <= 0 || radiusKm > 500)) {
            throw new IllegalArgumentException("Radius must be between 0 and 500 km");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("minPrice must not exceed maxPrice");
        }
        if (minimumRating != null && (minimumRating < 0 || minimumRating > 5)) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        if ((latitude == null) != (longitude == null)) {
            throw new IllegalArgumentException("Latitude and longitude must be supplied together");
        }
    }
}
