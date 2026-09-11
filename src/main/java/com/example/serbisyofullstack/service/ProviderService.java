package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.provider.CreateProviderProfileRequest;
import com.example.serbisyofullstack.dto.request.provider.UpdateProviderProfileRequest;
import com.example.serbisyofullstack.dto.nested.ProviderSummaryDto;
import com.example.serbisyofullstack.dto.response.provider.CreateProviderProfileResponse;
import com.example.serbisyofullstack.dto.response.provider.ReviewProviderDocumentResponse;
import com.example.serbisyofullstack.dto.response.provider.UpdateProviderProfileResponse;
import com.example.serbisyofullstack.model.entity.ProviderProfile;

/**
 * Provider profile lifecycle: creation, update, activation, verification state
 * and ownership checks. Verification is restricted to admins.
 */
public interface ProviderService {

    ProviderSummaryDto getProviderProfile(Long providerId);

    ProviderSummaryDto getOwnProfile(Long currentUserId);

    CreateProviderProfileResponse createProfile(Long userId, CreateProviderProfileRequest request);

    UpdateProviderProfileResponse updateProfile(Long currentUserId, UpdateProviderProfileRequest request);

    void activateProvider(Long currentUserId);

    void deactivateProvider(Long currentUserId);

    ReviewProviderDocumentResponse reviewProviderDocument(
            Long adminUserId,
            Long documentId,
            boolean approved,
            String notes
    );

    void assertOwnership(ProviderProfile provider, Long currentUserId);
}
