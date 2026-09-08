package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByProviderId(Long providerId);

    Optional<Booking> findByIdAndCustomerId(Long bookingId, Long customerId);

    Optional<Booking> findByIdAndProviderId(Long bookingId, Long providerId);

    List<Booking> findByCustomerIdOrderByScheduledStartDesc(Long customerId);

    List<Booking> findByProviderIdOrderByScheduledStartDesc(Long providerId);
}
