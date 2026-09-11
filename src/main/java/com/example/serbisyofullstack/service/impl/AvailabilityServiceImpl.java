package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.AvailabilityScheduleDto;
import com.example.serbisyofullstack.dto.nested.AvailabilityExceptionDto;
import com.example.serbisyofullstack.dto.request.availability.CreateAvailabilityExceptionRequest;
import com.example.serbisyofullstack.dto.request.availability.CreateAvailabilityScheduleRequest;
import com.example.serbisyofullstack.dto.request.availability.UpdateAvailabilityExceptionRequest;
import com.example.serbisyofullstack.dto.request.availability.UpdateAvailabilityScheduleRequest;
import com.example.serbisyofullstack.dto.response.availability.CreateAvailabilityExceptionResponse;
import com.example.serbisyofullstack.dto.response.availability.CreateAvailabilityScheduleResponse;
import com.example.serbisyofullstack.dto.response.availability.UpdateAvailabilityExceptionResponse;
import com.example.serbisyofullstack.dto.response.availability.UpdateAvailabilityScheduleResponse;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.AvailabilityExceptionMapper;
import com.example.serbisyofullstack.mapper.AvailabilityScheduleMapper;
import com.example.serbisyofullstack.model.entity.AvailabilityException;
import com.example.serbisyofullstack.model.entity.AvailabilitySchedule;
import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.enums.ExceptionType;
import com.example.serbisyofullstack.repository.AvailabilityExceptionRepository;
import com.example.serbisyofullstack.repository.AvailabilityScheduleRepository;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import com.example.serbisyofullstack.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Provider availability: recurring weekly schedules, one-day exceptions,
 * overlap prevention and availability checks for requested booking windows.
 * Schedules store provider-local weekday times with an IANA timezone;
 * availability checks convert the requested UTC window into that zone.
 */
@Service
@RequiredArgsConstructor
public class AvailabilityServiceImpl implements com.example.serbisyofullstack.service.AvailabilityService {

    private final AvailabilityScheduleRepository scheduleRepository;
    private final AvailabilityExceptionRepository exceptionRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final AvailabilityScheduleMapper scheduleMapper;
    private final AvailabilityExceptionMapper exceptionMapper;

    @Override
    @Transactional
    public CreateAvailabilityScheduleResponse createSchedule(Long providerUserId, CreateAvailabilityScheduleRequest request) {
        validateWindow(request.getStartTime(), request.getEndTime());
        validateTimezone(request.getTimezone());
        ProviderProfile provider = requireOwnProvider(providerUserId);
        assertNoOverlap(provider.getProviderProfileId(), request.getWeekday(), request.getStartTime(), request.getEndTime(), null);

        var schedule = scheduleMapper.toEntity(request);
        schedule.setProvider(provider);
        schedule.setActive(true);
        schedule = scheduleRepository.save(schedule);

        CreateAvailabilityScheduleResponse response = new CreateAvailabilityScheduleResponse();
        response.setSchedule(scheduleMapper.toDto(schedule));
        return response;
    }

    @Override
    @Transactional
    public UpdateAvailabilityScheduleResponse updateSchedule(Long providerUserId, Long scheduleId,
            UpdateAvailabilityScheduleRequest request) {
        var schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        assertOwnership(schedule.getProvider(), providerUserId);
        scheduleMapper.toUpdate(request, schedule);
        schedule = scheduleRepository.save(schedule);
        UpdateAvailabilityScheduleResponse response = new UpdateAvailabilityScheduleResponse();
        response.setSchedule(scheduleMapper.toDto(schedule));
        return response;
    }

    @Override
    @Transactional
    public void deleteSchedule(Long providerUserId, Long scheduleId) {
        var schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        assertOwnership(schedule.getProvider(), providerUserId);
        scheduleRepository.delete(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityScheduleDto> getSchedules(Long providerId) {
        return scheduleRepository.findByProviderIdAndActiveTrue(providerId).stream()
                .map(scheduleMapper::toDto).toList();
    }

    @Override
    @Transactional
    public CreateAvailabilityExceptionResponse createException(Long providerUserId, CreateAvailabilityExceptionRequest request) {
        if (request.getExceptionType() == ExceptionType.SPECIAL_HOURS) {
            if (request.getStartTime() == null || request.getEndTime() == null
                    || !request.getEndTime().isAfter(request.getStartTime())) {
                throw new ValidationException("Custom hours require start and end times, with end after start");
            }
        }
        ProviderProfile provider = requireOwnProvider(providerUserId);
        var exception = exceptionMapper.toEntity(request);
        exception.setProvider(provider);
        exception.setAvailable(request.getAvailable() != null ? request.getAvailable() : false);
        exception = exceptionRepository.save(exception);

        CreateAvailabilityExceptionResponse response = new CreateAvailabilityExceptionResponse();
        response.setException(exceptionMapper.toDto(exception));
        return response;
    }

    @Override
    @Transactional
    public UpdateAvailabilityExceptionResponse updateException(Long providerUserId, Long exceptionId,
            UpdateAvailabilityExceptionRequest request) {
        var exception = exceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Exception not found"));
        assertOwnership(exception.getProvider(), providerUserId);
        exceptionMapper.toUpdate(request, exception);
        exception = exceptionRepository.save(exception);
        UpdateAvailabilityExceptionResponse response = new UpdateAvailabilityExceptionResponse();
        response.setException(exceptionMapper.toDto(exception));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityExceptionDto> getExceptions(Long providerId) {
        return exceptionRepository.findByProviderIdAndDateBetween(providerId, LocalDate.now().minusYears(1), LocalDate.now().plusYears(1))
                .stream().map(exceptionMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAvailable(Long providerId, LocalDateTime startUtc, LocalDateTime endUtc) {
        List<AvailabilitySchedule> schedules = scheduleRepository.findByProviderIdAndActiveTrue(providerId);
        if (schedules.isEmpty()) {
            return false; // unconfigured providers are not bookable
        }

        // Convert UTC window into the provider's timezone (from its schedule).
        ZoneId zone = schedules.isEmpty() ? ZoneOffset.UTC
                : safeZone(schedules.get(0).getTimezone());
        LocalDateTime startLocal = LocalDateTime.ofInstant(startUtc.toInstant(ZoneOffset.UTC), zone);
        LocalDateTime endLocal = LocalDateTime.ofInstant(endUtc.toInstant(ZoneOffset.UTC), zone);

        // Walk each day in the requested window.
        for (LocalDate day = startLocal.toLocalDate(); !day.isAfter(endLocal.toLocalDate()); day = day.plusDays(1)) {
            var dayExceptions = exceptionRepository.findByProviderIdAndDateBetween(providerId, day, day);
            for (AvailabilityException exception : dayExceptions) {
                if (Boolean.FALSE.equals(exception.getAvailable())) {
                    return false; // provider blocked this day entirely
                }
            }

            DayOfWeek weekday = day.getDayOfWeek();
            List<AvailabilitySchedule> windows = schedules.stream()
                    .filter(s -> s.getWeekday() == weekday && Boolean.TRUE.equals(s.getActive()))
                    .toList();
            if (windows.isEmpty()) {
                return false; // no working window that day
            }
            for (AvailabilitySchedule window : windows) {
                LocalDateTime windowStart = LocalDateTime.of(day, window.getStartTime());
                LocalDateTime windowEnd = LocalDateTime.of(day, window.getEndTime());
                if (!startLocal.isBefore(windowStart) && !endLocal.isAfter(windowEnd)) {
                    return true; // fully inside one working window
                }
            }
        }
        return false;
    }

    // ---------- helpers ----------
    private void validateWindow(LocalTime start, LocalTime end) {
        if (!end.isAfter(start)) {
            throw new ValidationException("End time must be after start time");
        }
    }

    private void validateTimezone(String timezone) {
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            throw new ValidationException("Unknown timezone: " + timezone);
        }
    }

    private ZoneId safeZone(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (Exception e) {
            return ZoneOffset.UTC;
        }
    }

    private void assertOwnership(ProviderProfile provider, Long currentUserId) {
        if (provider == null || provider.getUser() == null
                || !provider.getUser().getUserId().equals(currentUserId)) {
            throw new ForbiddenException("You do not own this availability entry");
        }
    }

    private void assertNoOverlap(Long providerId, DayOfWeek weekday, LocalTime start, LocalTime end, Long ignoreId) {
        boolean overlaps = scheduleRepository.findByProviderIdAndActiveTrue(providerId).stream()
                .filter(s -> !s.getAvailabilityScheduleId().equals(ignoreId))
                .filter(s -> s.getWeekday() == weekday)
                .anyMatch(s -> start.isBefore(s.getEndTime()) && end.isAfter(s.getStartTime()));
        if (overlaps) {
            throw new ValidationException("Schedule overlaps an existing working window");
        }
    }

    private ProviderProfile requireOwnProvider(Long providerUserId) {
        return providerProfileRepository.findByUserId(providerUserId)
                .orElseThrow(() -> new ResourceNotFoundException("No provider profile for the current user"));
    }
}
