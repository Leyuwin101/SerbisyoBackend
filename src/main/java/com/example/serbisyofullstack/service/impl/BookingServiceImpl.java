package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.BookingItemDto;
import com.example.serbisyofullstack.dto.nested.BookingSummaryDto;
import com.example.serbisyofullstack.dto.request.booking.CreateBookingRequest;
import com.example.serbisyofullstack.dto.request.booking.UpdateBookingRequest;
import com.example.serbisyofullstack.dto.response.booking.CreateBookingResponse;
import com.example.serbisyofullstack.dto.response.booking.UpdateBookingResponse;
import com.example.serbisyofullstack.exception.BookingStateException;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.BookingMapper;
import com.example.serbisyofullstack.mapper.BookingItemMapper;
import com.example.serbisyofullstack.model.entity.*;
import com.example.serbisyofullstack.model.enums.BookingStatus;
import com.example.serbisyofullstack.repository.*;
import com.example.serbisyofullstack.service.AvailabilityService;
import com.example.serbisyofullstack.service.BookingService;
import com.example.serbisyofullstack.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.EnumSet;

/**
 * Owns the booking state machine:
 *
 * <pre>
 * PENDING -> CONFIRMED -> IN_PROGRESS -> COMPLETED
 * terminal: CANCELLED (customer), REJECTED (provider), EXPIRED (scheduler)
 * </pre>
 *
 * Every transition verifies current state + actor, persists the change and
 * appends an immutable BookingStatusHistory entry. Notifications are emitted
 * via NotificationService, never inline.
 */
@Slf4j
@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class BookingServiceImpl implements com.example.serbisyofullstack.service.BookingService {

    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository historyRepository;
    private final BookingItemRepository bookingItemRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final ServiceRepository serviceRepository;
    private final AddressRepository addressRepository;
    private final AvailabilityService availabilityService;
    private final NotificationService notificationService;
    private final BookingMapper bookingMapper;
    private final BookingItemMapper bookingItemMapper;

    private static final Set<BookingStatus> TERMINAL
            = EnumSet.of(BookingStatus.CANCELLED, BookingStatus.REJECTED, BookingStatus.EXPIRED);

    @Override
    @Transactional
    public CreateBookingResponse createBooking(Long customerId, CreateBookingRequest request) {
        CustomerProfile customer = customerProfileRepository.findByUserId(customerId)
                .orElseThrow(() -> new ValidationException("Customer profile not found for the current user"));

        ProviderProfile provider = providerProfileRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ValidationException("Provider not found"));
        Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ValidationException("Service not found"));
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ValidationException("Address not found"));

        // Ownership + relationship validation
        // Address stores a plain ownerId column (no User association), so we
        // compare it directly against the requesting user's id.
        if (!address.getOwnerId().equals(customerId)) {
            throw new ForbiddenException("Address does not belong to the current customer");
        }
        if (!service.getProvider().getProviderProfileId().equals(provider.getProviderProfileId())) {
            throw new ValidationException("Service does not belong to the selected provider");
        }
        if (!Boolean.TRUE.equals(service.getActive())) {
            throw new ValidationException("Service is not available for booking");
        }

        LocalDateTime start = request.getScheduledStart().toLocalDateTime();
        LocalDateTime end = request.getScheduledEnd().toLocalDateTime();
        if (!end.isAfter(start)) {
            throw new ValidationException("Scheduled end must be after scheduled start");
        }
        if (!availabilityService.isAvailable(provider.getProviderProfileId(), start, end)) {
            throw new BookingStateException("Provider is not available for the requested period");
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setCustomer(customer);
        booking.setProvider(provider);
        booking.setService(service);
        booking.setAddress(address);
        booking.setStatus(BookingStatus.PENDING);
        booking.setQuotedAmount(service.getBasePrice());
        booking = bookingRepository.save(booking);

        List<BookingItemDto> itemDtos = new java.util.ArrayList<>();
        if (request.getItems() != null) {
            for (var itemRequest : request.getItems()) {
                BookingItem item = bookingItemMapper.toEntity(itemRequest);
                item.setBooking(booking);
                item.setService(service);
                item.setName(service.getName());
                item.setUnitPrice(service.getBasePrice()); // authoritative price from the catalog
                item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                bookingItemRepository.save(item);
                itemDtos.add(bookingItemMapper.toDto(item));
            }
        }

        recordHistory(booking, null, BookingStatus.PENDING, null);
        notificationService.notifyUser(provider.getUser().getUserId(),
                "New booking request", "You have a new booking request for " + service.getName());

        CreateBookingResponse response = new CreateBookingResponse();
        response.setBooking(bookingMapper.toDto(booking));
        response.setItems(itemDtos);
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BookingSummaryDto getBooking(Long currentUserId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        assertParticipant(booking, currentUserId);
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public UpdateBookingResponse updateBooking(Long customerId, Long bookingId, UpdateBookingRequest request) {
        Booking booking = bookingRepository.findByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingStateException("Only PENDING bookings can be updated");
        }
        bookingMapper.toUpdate(request, booking);
        booking = bookingRepository.save(booking);
        UpdateBookingResponse response = new UpdateBookingResponse();
        response.setBooking(bookingMapper.toDto(booking));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingSummaryDto> listBookingsForUser(Long currentUserId, BookingStatus status, Pageable pageable) {
        // Both customers and providers query their own bookings; ownership is
        // enforced by matching either side.
        Page<Booking> page = bookingRepository.searchByParticipant(currentUserId, status, pageable);
        return page.map(bookingMapper::toDto);
    }

    @Override
    @Transactional
    public BookingSummaryDto acceptBooking(Long providerUserId, Long bookingId) {
        return transition(bookingId, providerUserId, BookingStatus.CONFIRMED, null);
    }

    @Override
    @Transactional
    public BookingSummaryDto declineBooking(Long providerUserId, Long bookingId, String reason) {
        return transition(bookingId, providerUserId, BookingStatus.REJECTED, reason);
    }

    @Override
    @Transactional
    public BookingSummaryDto startBooking(Long providerUserId, Long bookingId) {
        return transition(bookingId, providerUserId, BookingStatus.IN_PROGRESS, null);
    }

    @Override
    @Transactional
    public BookingSummaryDto completeBooking(Long providerUserId, Long bookingId) {
        return transition(bookingId, providerUserId, BookingStatus.COMPLETED, null);
    }

    @Override
    @Transactional
    public BookingSummaryDto cancelBooking(Long customerId, Long bookingId, String reason) {
        return transition(bookingId, customerId, BookingStatus.CANCELLED, reason);
    }

    @Override
    @Transactional
    public void expireStaleBookings() {
        LocalDateTime cutoff = LocalDateTime.now();
        List<Booking> stale = bookingRepository.findByStatusAndScheduledEndBefore(BookingStatus.PENDING, cutoff);
        for (Booking booking : stale) {
            recordHistory(booking, booking.getStatus(), BookingStatus.EXPIRED, "Auto-expired by scheduler");
            booking.setStatus(BookingStatus.EXPIRED);
            bookingRepository.save(booking);
            notificationService.notifyUser(booking.getCustomer().getUser().getUserId(),
                    "Booking expired", "Your booking request was not accepted in time and has expired.");
        }
    }

    // ---------- state machine helpers ----------
    private BookingSummaryDto transition(Long bookingId, Long actorUserId, BookingStatus target, String note) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        boolean isProvider = booking.getProvider().getUser().getUserId().equals(actorUserId);
        boolean isCustomer = booking.getCustomer().getUser().getUserId().equals(actorUserId);

        // 1. verify the actor
        switch (target) {
            case CONFIRMED, REJECTED, IN_PROGRESS, COMPLETED -> {
                if (!isProvider) {
                    throw new ForbiddenException("Only the booked provider can perform this action");
                }
            }
            case CANCELLED -> {
                if (!isCustomer) {
                    throw new ForbiddenException("Only the booking customer can cancel");
                }
            }
            default ->
                throw new BookingStateException("Unsupported transition target: " + target);
        }

        // 2. verify the transition is legal from the current state
        boolean allowed = switch (booking.getStatus()) {
            case PENDING ->
                target == BookingStatus.CONFIRMED
                || target == BookingStatus.REJECTED
                || target == BookingStatus.CANCELLED;
            case CONFIRMED ->
                target == BookingStatus.IN_PROGRESS
                || target == BookingStatus.CANCELLED
                || target == BookingStatus.REJECTED;
            case IN_PROGRESS ->
                target == BookingStatus.COMPLETED;
            default ->
                false;
        };
        if (!allowed) {
            throw new BookingStateException("Cannot move booking from " + booking.getStatus() + " to " + target);
        }

        // 3. update + 4. immutable history
        recordHistory(booking, booking.getStatus(), target, note);
        booking.setStatus(target);
        booking = bookingRepository.save(booking);

        // 5. notifications for the other party
        Long otherPartyId = isProvider
                ? booking.getCustomer().getUser().getUserId()
                : booking.getProvider().getUser().getUserId();
        notificationService.notifyUser(otherPartyId,
                "Booking " + target.name().toLowerCase(),
                "Booking #" + booking.getBookingId() + " is now " + target.name());

        return bookingMapper.toDto(booking);
    }

    private void assertParticipant(Booking booking, Long currentUserId) {
        Long customerUserId = booking.getCustomer().getUser().getUserId();
        Long providerUserId = booking.getProvider().getUser().getUserId();
        if (!customerUserId.equals(currentUserId) && !providerUserId.equals(currentUserId)) {
            throw new ForbiddenException("You do not have access to this booking");
        }
    }

    private void recordHistory(Booking booking, BookingStatus oldStatus, BookingStatus newStatus, String notes) {
        BookingStatusHistory history = new BookingStatusHistory();
        history.setBooking(booking);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setNotes(notes);
        historyRepository.save(history);
    }
}
