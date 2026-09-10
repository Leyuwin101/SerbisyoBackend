package com.example.serbisyofullstack.dto.response.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.model.enums.RoleEnum;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response body after an admin replaces a user's roles. Echoes the effective
 * role set so the client can confirm what was actually applied.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRolesResponse {

    @Schema(description = "The user whose roles were changed.")
    private UserSummaryDto user;

    @Schema(description = "The full set of roles now assigned to the user.")
    private Set<RoleEnum> roles;
}
