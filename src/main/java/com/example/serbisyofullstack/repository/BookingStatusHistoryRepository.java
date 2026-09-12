package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.BookingStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, Long> {

    @org.springframework.data.jpa.repository.Query("select h from BookingStatusHistory h where h.booking.bookingId = :bookingId order by h.changedAt asc")
    List<BookingStatusHistory> findByBookingIdOrderByCreatedAtAsc(Long bookingId);
}
