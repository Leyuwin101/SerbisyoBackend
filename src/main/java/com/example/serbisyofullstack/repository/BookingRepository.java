package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @org.springframework.data.jpa.repository.Query("select b from Booking b where b.customer.customerProfileId = :customerId")
    List<Booking> findByCustomerId(Long customerId);

    @org.springframework.data.jpa.repository.Query("select b from Booking b where b.provider.providerProfileId = :providerId")
    List<Booking> findByProviderId(Long providerId);

    @org.springframework.data.jpa.repository.Query(
            "select b from Booking b where b.bookingId = :bookingId and b.customer.customerProfileId = :customerId")
    Optional<Booking> findByIdAndCustomerId(Long bookingId, Long customerId);

    @org.springframework.data.jpa.repository.Query(
            "select b from Booking b where b.bookingId = :bookingId and b.provider.providerProfileId = :providerId")
    Optional<Booking> findByIdAndProviderId(Long bookingId, Long providerId);

    @org.springframework.data.jpa.repository.Query(
            "select b from Booking b where b.customer.customerProfileId = :customerId order by b.scheduledStart desc")
    List<Booking> findByCustomerIdOrderByScheduledStartDesc(Long customerId);

    @org.springframework.data.jpa.repository.Query(
            "select b from Booking b where b.provider.providerProfileId = :providerId order by b.scheduledStart desc")
    List<Booking> findByProviderIdOrderByScheduledStartDesc(Long providerId);

    List<Booking> findByStatusAndScheduledEndBefore(com.example.serbisyofullstack.model.enums.BookingStatus status,
                                                   java.time.LocalDateTime cutoff);

    @org.springframework.data.jpa.repository.Query(
            "select b from Booking b where (b.customer.user.userId = :userId or b.provider.user.userId = :userId)"
                    + " and (:status is null or b.status = :status)")
    Page<Booking> searchByParticipant(@org.springframework.lang.NonNull Long userId,
                                      com.example.serbisyofullstack.model.enums.BookingStatus status,
                                      Pageable pageable);
}
