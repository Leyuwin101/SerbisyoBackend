package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.availability.CreateAvailabilityExceptionRequest;
import com.example.serbisyofullstack.dto.request.availability.CreateAvailabilityScheduleRequest;
import com.example.serbisyofullstack.dto.request.availability.UpdateAvailabilityExceptionRequest;
import com.example.serbisyofullstack.dto.request.availability.UpdateAvailabilityScheduleRequest;
import com.example.serbisyofullstack.dto.nested.AvailabilityExceptionDto;
import com.example.serbisyofullstack.dto.nested.AvailabilityScheduleDto;
import com.example.serbisyofullstack.dto.response.availability.CreateAvailabilityExceptionResponse;
import com.example.serbisyofullstack.dto.response.availability.CreateAvailabilityScheduleResponse;
import com.example.serbisyofullstack.dto.response.availability.UpdateAvailabilityExceptionResponse;
import com.example.serbisyofullstack.dto.response.availability.UpdateAvailabilityScheduleResponse;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Provider availability: recurring schedules, exceptions, overlap prevention
 * and availability checks for requested booking windows. Timestamps are stored
 * in UTC.
 */
public interface AvailabilityService {

    CreateAvailabilityScheduleResponse createSchedule(Long providerUserId, CreateAvailabilityScheduleRequest request);

    UpdateAvailabilityScheduleResponse updateSchedule(Long providerUserId, Long scheduleId, UpdateAvailabilityScheduleRequest request);

    void deleteSchedule(Long providerUserId, Long scheduleId);

    List<AvailabilityScheduleDto> getSchedules(Long providerId);

    CreateAvailabilityExceptionResponse createException(Long providerUserId, CreateAvailabilityExceptionRequest request);

    UpdateAvailabilityExceptionResponse updateException(Long providerUserId, Long exceptionId, UpdateAvailabilityExceptionRequest request);

    List<AvailabilityExceptionDto> getExceptions(Long providerId);

    boolean isAvailable(Long providerId, LocalDateTime startUtc, LocalDateTime endUtc);
}
