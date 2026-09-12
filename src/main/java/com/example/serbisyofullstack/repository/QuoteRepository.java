package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    @org.springframework.data.jpa.repository.Query("select q from Quote q where q.booking.bookingId = :bookingId order by q.createdAt desc")
    List<Quote> findByBookingIdOrderByCreatedAtDesc(Long bookingId);

    @org.springframework.data.jpa.repository.Query("select q from Quote q where q.booking.bookingId = :bookingId and q.status = :status")
    Optional<Quote> findByBookingIdAndStatus(Long bookingId, String status);
}
