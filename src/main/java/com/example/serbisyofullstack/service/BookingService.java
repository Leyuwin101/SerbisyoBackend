package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.booking.CreateBookingRequest;
import com.example.serbisyofullstack.dto.request.booking.UpdateBookingRequest;
import com.example.serbisyofullstack.dto.nested.BookingSummaryDto;
import com.example.serbisyofullstack.dto.response.booking.CreateBookingResponse;
import com.example.serbisyofullstack.dto.response.booking.UpdateBookingResponse;
import com.example.serbisyofullstack.model.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Booking lifecycle. Owns the booking state machine:
 *
 * <pre>
 * PENDING -> CONFIRMED(ACCEPTED) -> PROVIDER_ON_THE_WAY -> IN_PROGRESS -> COMPLETED
 * terminal: CANCELLED (customer), REJECTED (provider), EXPIRED (scheduler)
 * </pre>
 *
 * Every transition verifies state + actor, updates the booking and appends an
 * immutable {@code BookingStatusHistory} entry.
 */
public interface BookingService {

    CreateBookingResponse createBooking(Long customerId, CreateBookingRequest request);

    BookingSummaryDto getBooking(Long currentUserId, Long bookingId);

    UpdateBookingResponse updateBooking(Long customerId, Long bookingId, UpdateBookingRequest request);

    Page<BookingSummaryDto> listBookingsForUser(Long currentUserId, BookingStatus status, Pageable pageable);

    BookingSummaryDto acceptBooking(Long providerUserId, Long bookingId);

    BookingSummaryDto declineBooking(Long providerUserId, Long bookingId, String reason);

    BookingSummaryDto startBooking(Long providerUserId, Long bookingId);

    BookingSummaryDto completeBooking(Long providerUserId, Long bookingId);

    BookingSummaryDto cancelBooking(Long customerId, Long bookingId, String reason);

    void expireStaleBookings();
}
