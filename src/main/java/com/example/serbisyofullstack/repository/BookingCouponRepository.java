package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.BookingCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingCouponRepository extends JpaRepository<BookingCoupon, Long> {

    @org.springframework.data.jpa.repository.Query("select c from BookingCoupon c where c.booking.bookingId = :bookingId")
    Optional<BookingCoupon> findByBookingId(Long bookingId);

    @org.springframework.data.jpa.repository.Query("select count(c) > 0 from BookingCoupon c where c.booking.bookingId = :bookingId")
    boolean existsByBookingId(Long bookingId);
}
