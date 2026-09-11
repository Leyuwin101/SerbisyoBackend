package com.example.serbisyofullstack.scheduler;

import com.example.serbisyofullstack.model.entity.Booking;
import com.example.serbisyofullstack.model.entity.BookingStatusHistory;
import com.example.serbisyofullstack.model.enums.BookingStatus;
import com.example.serbisyofullstack.repository.BookingRepository;
import com.example.serbisyofullstack.repository.BookingStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Marks bookings whose scheduled end has passed while still PENDING or
 * CONFIRMED as EXPIRED, recording an immutable status-history entry for each
 * transition (actor = system, i.e. null). Centralized here so no controller or
 * repository ever sets EXPIRED directly.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingExpirationScheduler {

    private final BookingRepository bookingRepository;
    private final BookingStatusHistoryRepository historyRepository;

    @Scheduled(fixedDelayString = "${app.scheduler.booking-expiration-interval-ms:300000}")
    @Transactional
    public void expireStaleBookings() {
        LocalDateTime now = LocalDateTime.now();
        int total = 0;
        for (BookingStatus status : List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)) {
            List<Booking> stale = bookingRepository.findByStatusAndScheduledEndBefore(status, now);
            for (Booking booking : stale) {
                BookingStatus old = booking.getStatus();
                booking.setStatus(BookingStatus.EXPIRED);
                BookingStatusHistory history = new BookingStatusHistory();
                history.setBooking(booking);
                history.setOldStatus(old);
                history.setNewStatus(BookingStatus.EXPIRED);
                history.setNotes("Expired automatically: scheduled end passed");
                historyRepository.save(history);
                bookingRepository.save(booking);
                total++;
            }
        }
        if (total > 0) {
            log.info("Expired {} stale bookings", total);
        }
    }
}
