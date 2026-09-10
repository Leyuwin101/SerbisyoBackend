package com.example.serbisyofullstack.dto.request.admin;

import io.swagger.v3.oas.annotations.media.Schema;

import com.example.serbisyofullstack.model.enums.RoleEnum;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Admin-only request body that replaces the full set of roles assigned to a
 * user. Each role maps to a {@link com.example.serbisyofullstack.model.entity.Role}
 * row linked through {@link com.example.serbisyofullstack.model.entity.UserRole}.
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRoleRequest {
    @Schema(description = "The complete new set of roles; the previous set is discarded.")
    @NotEmpty(message = "At least one role is required")
    private Set<RoleEnum> roles;
}
