package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProviderIdOrderByCreatedAtDesc(Long providerId);

    Optional<Review> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);

    Page<Review> findByProviderId(Long providerId, Pageable pageable);

    Page<Review> findByCustomerId(Long customerId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("select coalesce(avg(r.rating), null) from Review r where r.provider.providerProfileId = :providerId")
    Double findAverageRatingByProviderId(Long providerId);

    long countByProviderId(Long providerId);
}
