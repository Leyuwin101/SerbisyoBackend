package com.example.serbisyofullstack.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.dto.nested.UserSummaryDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after updating a user's core account details. Returns the
 * updated user as a {@link UserSummaryDto} so the client can refresh its state
 * without a second request.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateUserResponse {

    @Schema(description = "Updated account details (id, email, phone, status).")
    private UserSummaryDto user;
}
