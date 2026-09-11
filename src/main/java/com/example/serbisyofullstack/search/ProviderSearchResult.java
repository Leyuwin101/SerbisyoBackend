package com.example.serbisyofullstack.search;

/**
 * Result row for provider discovery. Contains only public, display-oriented
 * data — never payout, document or other private provider fields.
 */
public record ProviderSearchResult(
        Long providerId,
        Long userId,
        String businessName,
        String bio,
        String verificationStatus,
        Double averageRating,
        Integer reviewCount,
        Double distanceKm
        ) {

}
