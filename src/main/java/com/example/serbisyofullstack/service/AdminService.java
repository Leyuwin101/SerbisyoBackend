package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.admin.UpdateUserRoleRequest;
import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.dto.response.admin.UpdateUserRolesResponse;
import com.example.serbisyofullstack.model.enums.VerificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Administrative operations: role management, provider verification queue and
 * platform moderation entry points. Every action is audited.
 */
public interface AdminService {

    UpdateUserRolesResponse updateUserRoles(Long adminUserId, UpdateUserRoleRequest request);

    void setProviderVerification(Long adminUserId, Long providerId, VerificationStatus status, String notes);

    Page<UserSummaryDto> listUsersByStatus(String status, Pageable pageable);
}
