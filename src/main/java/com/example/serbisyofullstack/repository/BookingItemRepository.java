package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingItemRepository extends JpaRepository<BookingItem, Long> {

    @org.springframework.data.jpa.repository.Query("select i from BookingItem i where i.booking.bookingId = :bookingId")
    List<BookingItem> findByBookingId(Long bookingId);
}
