package com.example.serbisyofullstack.dto.request.booking;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.validation.EndAfterStart;

import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * Request body for partially updating an existing booking (reschedule,
 * change of address, or note). Allowed transitions depend on the booking's
 * current status, which the service layer enforces. All fields optional.
 */
@Getter
@Setter
@NoArgsConstructor
@EndAfterStart(startField = "scheduledStart", endField = "scheduledEnd", message = "Scheduled end must be after scheduled start")
public class UpdateBookingRequest {
    @Schema(description = "New start time; must be in the future when supplied.")
    @Future(message = "Scheduled start must be in the future")
    private OffsetDateTime scheduledStart;

    @Schema(description = "New end time; must be in the future when supplied.")
    @Future(message = "Scheduled end must be in the future")
    private OffsetDateTime scheduledEnd;

    @Schema(description = "New service address; must belong to the booking's customer.")
    private Long addressId;

    @Schema(description = "Updated instructions from the customer.")
    private String customerNote;
}
