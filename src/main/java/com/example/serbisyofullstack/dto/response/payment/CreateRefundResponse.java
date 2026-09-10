package com.example.serbisyofullstack.dto.response.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

import com.example.serbisyofullstack.dto.nested.RefundDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body for a refund request. The nested {@link RefundDto} carries the
 * refund's initial status (usually PENDING until the gateway confirms) and the
 * payment it applies to.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateRefundResponse {

    @Schema(description = "The created refund record.")
    private RefundDto refund;

    @Schema(description = "Server timestamp of creation.")
    private LocalDateTime createdAt;
}
