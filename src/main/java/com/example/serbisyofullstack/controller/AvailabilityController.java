package com.example.serbisyofullstack.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.serbisyofullstack.dto.nested.AvailabilityExceptionDto;
import com.example.serbisyofullstack.dto.nested.AvailabilityScheduleDto;
import com.example.serbisyofullstack.dto.request.availability.CreateAvailabilityExceptionRequest;
import com.example.serbisyofullstack.dto.request.availability.CreateAvailabilityScheduleRequest;
import com.example.serbisyofullstack.dto.request.availability.UpdateAvailabilityExceptionRequest;
import com.example.serbisyofullstack.dto.request.availability.UpdateAvailabilityScheduleRequest;
import com.example.serbisyofullstack.dto.response.availability.CreateAvailabilityExceptionResponse;
import com.example.serbisyofullstack.dto.response.availability.CreateAvailabilityScheduleResponse;
import com.example.serbisyofullstack.dto.response.availability.UpdateAvailabilityExceptionResponse;
import com.example.serbisyofullstack.dto.response.availability.UpdateAvailabilityScheduleResponse;
import com.example.serbisyofullstack.security.CurrentUserService;
import com.example.serbisyofullstack.service.AvailabilityService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Provider availability management. Only the owning provider can create, update
 * or delete schedules/exceptions; ownership is enforced in
 * {@link AvailabilityService}.
 */
@Tag(name = "Availability", description = "Provider weekly schedules and calendar exceptions")
@RestController
@RequestMapping("/api/v1/availability")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PROVIDER')")
public class AvailabilityController {

    private final AvailabilityService availabilityService;
    private final CurrentUserService currentUserService;

    // ---------- schedules ----------
    @Operation(summary = "Create a weekly schedule window")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Schedule created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a provider"),
        @ApiResponse(responseCode = "409", description = "Window overlaps an existing schedule")
    })
    @PostMapping("/schedules")
    public ResponseEntity<CreateAvailabilityScheduleResponse> createSchedule(
            @Valid @RequestBody CreateAvailabilityScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityService.createSchedule(
                        currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "Update a weekly schedule window")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Schedule updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "403", description = "Schedule belongs to another provider"),
        @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @PutMapping("/schedules/{scheduleId}")
    public ResponseEntity<UpdateAvailabilityScheduleResponse> updateSchedule(
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateAvailabilityScheduleRequest request) {
        return ResponseEntity.ok(availabilityService.updateSchedule(
                currentUserService.getCurrentUserId(), scheduleId, request));
    }

    @Operation(summary = "Delete a weekly schedule window")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Schedule deleted"),
        @ApiResponse(responseCode = "403", description = "Schedule belongs to another provider"),
        @ApiResponse(responseCode = "404", description = "Schedule not found")
    })
    @DeleteMapping("/schedules/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        availabilityService.deleteSchedule(currentUserService.getCurrentUserId(), scheduleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List the provider's weekly schedule windows")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Schedules retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a provider")
    })
    @GetMapping("/schedules")
    public ResponseEntity<List<AvailabilityScheduleDto>> getSchedules() {
        return ResponseEntity.ok(availabilityService.getSchedules(
                currentUserService.getCurrentUserId()));
    }

    // ---------- exceptions ----------
    @Operation(summary = "Create a calendar exception (day off or custom hours)")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Exception created"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "403", description = "Caller is not a provider"),
        @ApiResponse(responseCode = "409", description = "An exception already exists for that date")
    })
    @PostMapping("/exceptions")
    public ResponseEntity<CreateAvailabilityExceptionResponse> createException(
            @Valid @RequestBody CreateAvailabilityExceptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(availabilityService.createException(
                        currentUserService.getCurrentUserId(), request));
    }

    @Operation(summary = "Update a calendar exception")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Exception updated"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "403", description = "Exception belongs to another provider"),
        @ApiResponse(responseCode = "404", description = "Exception not found")
    })
    @PutMapping("/exceptions/{exceptionId}")
    public ResponseEntity<UpdateAvailabilityExceptionResponse> updateException(
            @PathVariable Long exceptionId,
            @Valid @RequestBody UpdateAvailabilityExceptionRequest request) {
        return ResponseEntity.ok(availabilityService.updateException(
                currentUserService.getCurrentUserId(), exceptionId, request));
    }

    @Operation(summary = "List the provider's calendar exceptions")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Exceptions retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Caller is not a provider")
    })
    @GetMapping("/exceptions")
    public ResponseEntity<List<AvailabilityExceptionDto>> getExceptions() {
        return ResponseEntity.ok(availabilityService.getExceptions(
                currentUserService.getCurrentUserId()));
    }
}
