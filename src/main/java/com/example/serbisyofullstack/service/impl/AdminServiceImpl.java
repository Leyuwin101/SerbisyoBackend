package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.audit.AuditService;
import com.example.serbisyofullstack.dto.nested.UserSummaryDto;
import com.example.serbisyofullstack.dto.request.admin.UpdateUserRoleRequest;
import com.example.serbisyofullstack.dto.response.admin.UpdateUserRolesResponse;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.UserMapper;
import com.example.serbisyofullstack.model.entity.Role;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.entity.UserRole;
import com.example.serbisyofullstack.model.enums.RoleEnum;
import com.example.serbisyofullstack.model.enums.Status;
import com.example.serbisyofullstack.model.enums.VerificationStatus;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import com.example.serbisyofullstack.repository.RoleRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.repository.UserRoleRepository;
import com.example.serbisyofullstack.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Administrative operations. Every action is written to the audit trail with
 * the acting admin's id — role changes and provider verification decisions are
 * security-sensitive and must be traceable.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final UserMapper userMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public UpdateUserRolesResponse updateUserRoles(Long adminUserId, UpdateUserRoleRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        final Set<UserRole> newRoles = request.getRoles().stream().map(roleEnum -> {
            Role role = roleRepository.findByName(roleEnum)
                    .orElseThrow(() -> new ValidationException("Unknown role: " + roleEnum));
            UserRole userRole = new UserRole();
            userRole.setUser(user);
            userRole.setRole(role);
            return userRole;
        }).collect(Collectors.toSet());

        user.getRoles().clear();
        user.getRoles().addAll(newRoles);
        User savedUser = userRepository.save(user);

        auditService.record(adminUserId, "USER_ROLES_UPDATED", "user", savedUser.getUserId(),
                "roles=" + request.getRoles());
        log.info("Admin {} updated roles of user {} to {}", adminUserId, savedUser.getUserId(), request.getRoles());

        UpdateUserRolesResponse response = new UpdateUserRolesResponse();
        response.setUser(userMapper.toDto(savedUser));
        response.setRoles(request.getRoles());
        return response;
    }

    @Override
    @Transactional
    public void setProviderVerification(Long adminUserId, Long providerId, VerificationStatus status, String notes) {
        var provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));
        provider.setVerificationStatus(status);
        providerProfileRepository.save(provider);
        auditService.record(adminUserId, "PROVIDER_VERIFICATION_SET", "provider", providerId,
                "status=" + status + (notes != null ? ",notes=" + notes : ""));
        log.info("Admin {} set provider {} verification to {}", adminUserId, providerId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserSummaryDto> listUsersByStatus(String status, Pageable pageable) {
        Status statusEnum;
        try {
            statusEnum = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown account status: " + status);
        }
        List<UserSummaryDto> users = userRepository.findByStatus(statusEnum).stream()
                .map(userMapper::toDto)
                .toList();
        return new PageImpl<>(users, pageable, users.size());
    }
}
