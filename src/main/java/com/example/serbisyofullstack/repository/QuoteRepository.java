package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findByBookingIdOrderByCreatedAtDesc(Long bookingId);

    Optional<Quote> findByBookingIdAndStatus(Long bookingId, String status);
}
