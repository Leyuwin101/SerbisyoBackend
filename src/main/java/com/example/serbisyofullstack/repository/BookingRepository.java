package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByCustomerId(Long customerId);

    List<Booking> findByProviderId(Long providerId);

    Optional<Booking> findByIdAndCustomerId(Long bookingId, Long customerId);

    Optional<Booking> findByIdAndProviderId(Long bookingId, Long providerId);

    List<Booking> findByCustomerIdOrderByScheduledStartDesc(Long customerId);

    List<Booking> findByProviderIdOrderByScheduledStartDesc(Long providerId);

    List<Booking> findByStatusAndScheduledEndBefore(com.example.serbisyofullstack.model.enums.BookingStatus status,
                                                   java.time.LocalDateTime cutoff);

    @org.springframework.data.jpa.repository.Query(
            "select b from Booking b where b.customer.user.userId = :userId or b.provider.user.userId = :userId"
                    + " and (:status is null or b.status = :status)")
    Page<Booking> searchByParticipant(@org.springframework.lang.NonNull Long userId,
                                      com.example.serbisyofullstack.model.enums.BookingStatus status,
                                      Pageable pageable);
}
