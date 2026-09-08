package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.BookingCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingCouponRepository extends JpaRepository<BookingCoupon, Long> {

    Optional<BookingCoupon> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);
}
