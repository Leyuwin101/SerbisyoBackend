package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @org.springframework.data.jpa.repository.Query("select r from Review r where r.provider.providerProfileId = :providerId order by r.createdAt desc")
    List<Review> findByProviderIdOrderByCreatedAtDesc(Long providerId);

    @org.springframework.data.jpa.repository.Query("select r from Review r where r.booking.bookingId = :bookingId")
    Optional<Review> findByBookingId(Long bookingId);

    @org.springframework.data.jpa.repository.Query("select count(r) > 0 from Review r where r.booking.bookingId = :bookingId")
    boolean existsByBookingId(Long bookingId);

    @org.springframework.data.jpa.repository.Query("select r from Review r where r.provider.providerProfileId = :providerId")
    Page<Review> findByProviderId(Long providerId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("select r from Review r where r.customer.customerProfileId = :customerId")
    Page<Review> findByCustomerId(Long customerId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("select coalesce(avg(r.rating), null) from Review r where r.provider.providerProfileId = :providerId")
    Double findAverageRatingByProviderId(Long providerId);

    @org.springframework.data.jpa.repository.Query("select count(r) from Review r where r.provider.providerProfileId = :providerId")
    long countByProviderId(Long providerId);
}
