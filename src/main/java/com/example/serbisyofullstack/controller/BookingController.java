package com.example.serbisyofullstack.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.api.PaginationGuard;
import com.example.serbisyofullstack.dto.nested.BookingSummaryDto;
import com.example.serbisyofullstack.dto.request.booking.CreateBookingRequest;
import com.example.serbisyofullstack.dto.request.booking.UpdateBookingRequest;
import com.example.serbisyofullstack.dto.response.booking.CreateBookingResponse;
import com.example.serbisyofullstack.dto.response.booking.UpdateBookingResponse;
import com.example.serbisyofullstack.model.enums.BookingStatus;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.BookingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Booking lifecycle endpoints. The customer id / provider id used by every
 * operation comes from the authenticated principal — client-supplied IDs in the
 * body can never select someone else's booking.
 */
@Tag(name = "Bookings", description = "Booking lifecycle for customers and providers")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final CurrentUserService currentUserService;

    @Operation(summary = "Create a booking")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Booking created with server-computed pricing"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Provider, service or address not found")
    })
    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "List the authenticated user's bookings, optionally by status")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Bookings retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<Page<BookingSummaryDto>> listBookings(
            @RequestParam(required = false) BookingStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(bookingService.listBookingsForUser(
                currentUserService.getCurrentUserId(), status, PaginationGuard.cap(pageable)));
    }

    @Operation(summary = "Get a single booking")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Booking belongs to another user"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookingSummaryDto> getBooking(
            @PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBooking(currentUserService.getCurrentUserId(), id));
    }

    @Operation(summary = "Update a booking's schedule, address or note")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Booking belongs to another user"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UpdateBookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequest request) {
        return ResponseEntity.ok(bookingService.updateBooking(
                currentUserService.getCurrentUserId(), id, request));
    }

    // ---------- provider actions ----------
    @Operation(summary = "Accept a pending booking (provider only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking accepted"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not the booking's provider or state forbids it"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{id}/accept")
    public ResponseEntity<BookingSummaryDto> acceptBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.acceptBooking(currentUserService.getCurrentUserId(), id));
    }

    @Operation(summary = "Decline a pending booking (provider only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking declined"),
        @ApiResponse(responseCode = "403", description = "Caller is not the booking's provider or state forbids it"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{id}/decline")
    public ResponseEntity<BookingSummaryDto> declineBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingService.declineBooking(currentUserService.getCurrentUserId(), id, reason));
    }

    @Operation(summary = "Start an accepted booking (provider only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking started"),
        @ApiResponse(responseCode = "403", description = "Caller is not the booking's provider or state forbids it"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{id}/start")
    public ResponseEntity<BookingSummaryDto> startBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.startBooking(currentUserService.getCurrentUserId(), id));
    }

    @Operation(summary = "Complete a started booking (provider only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking completed"),
        @ApiResponse(responseCode = "403", description = "Caller is not the booking's provider or state forbids it"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{id}/complete")
    public ResponseEntity<BookingSummaryDto> completeBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.completeBooking(currentUserService.getCurrentUserId(), id));
    }

    @Operation(summary = "Cancel a booking (customer or provider)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Booking cancelled"),
        @ApiResponse(responseCode = "403", description = "Caller is not a party to the booking or state forbids it"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<BookingSummaryDto> cancelBooking(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(bookingService.cancelBooking(currentUserService.getCurrentUserId(), id, reason));
    }
}
