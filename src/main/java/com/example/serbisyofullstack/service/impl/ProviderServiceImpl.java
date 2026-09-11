package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.audit.AuditService;
import com.example.serbisyofullstack.dto.nested.ProviderSummaryDto;
import com.example.serbisyofullstack.dto.request.provider.CreateProviderProfileRequest;
import com.example.serbisyofullstack.dto.request.provider.UpdateProviderProfileRequest;
import com.example.serbisyofullstack.dto.response.provider.CreateProviderProfileResponse;
import com.example.serbisyofullstack.dto.response.provider.ReviewProviderDocumentResponse;
import com.example.serbisyofullstack.dto.response.provider.UpdateProviderProfileResponse;
import com.example.serbisyofullstack.exception.ConflictException;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.mapper.ProviderDocumentMapper;
import com.example.serbisyofullstack.mapper.ProviderProfileMapper;
import com.example.serbisyofullstack.model.entity.ProviderDocument;
import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.enums.DocumentStatus;
import com.example.serbisyofullstack.model.enums.VerificationStatus;
import com.example.serbisyofullstack.repository.ProviderDocumentRepository;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Provider business operations: profile creation/updates, activation and
 * verification-state changes. Verification is admin-only and audited. Every
 * method resolves ownership from the database, never from the client.
 */
@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements com.example.serbisyofullstack.service.ProviderService {

    private final ProviderProfileRepository providerProfileRepository;
    private final ProviderDocumentRepository providerDocumentRepository;
    private final UserRepository userRepository;
    private final ProviderProfileMapper mapper;
    private final ProviderDocumentMapper providerDocumentMapper;
    private final com.example.serbisyofullstack.audit.AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public com.example.serbisyofullstack.dto.nested.ProviderSummaryDto getProviderProfile(Long providerId) {
        return providerProfileRepository.findById(providerId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public com.example.serbisyofullstack.dto.nested.ProviderSummaryDto getOwnProfile(Long currentUserId) {
        return providerProfileRepository.findByUserId(currentUserId)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("No provider profile for the current user"));
    }

    @Override
    @Transactional
    public CreateProviderProfileResponse createProfile(Long userId, CreateProviderProfileRequest request) {
        if (providerProfileRepository.findByUserId(userId).isPresent()) {
            throw new ConflictException("Provider profile already exists for this user");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ProviderProfile profile = mapper.toEntity(request);
        profile.setUser(user);
        profile.setVerificationStatus(VerificationStatus.PENDING);
        profile = providerProfileRepository.save(profile);

        CreateProviderProfileResponse response = new CreateProviderProfileResponse();
        response.setProvider(mapper.toDto(profile));
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    @Override
    @Transactional
    public UpdateProviderProfileResponse updateProfile(Long currentUserId, UpdateProviderProfileRequest request) {
        ProviderProfile profile = providerProfileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("No provider profile for the current user"));
        mapper.toUpdate(request, profile);
        profile = providerProfileRepository.save(profile);
        UpdateProviderProfileResponse response = new UpdateProviderProfileResponse();
        response.setProvider(mapper.toDto(profile));
        return response;
    }

    @Override
    @Transactional
    public void activateProvider(Long currentUserId) {
        ProviderProfile profile = requireOwnProfile(currentUserId);
        // Only verified providers may become publicly active.
        if (profile.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new ForbiddenException("Provider must be verified before activation");
        }
        profile.setVerificationStatus(VerificationStatus.VERIFIED);
        providerProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public void deactivateProvider(Long currentUserId) {
        requireOwnProfile(currentUserId);
        // Deactivation is modelled as reverting to PENDING until re-review.
        ProviderProfile profile = providerProfileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("No provider profile for the current user"));
        profile.setVerificationStatus(VerificationStatus.PENDING);
        providerProfileRepository.save(profile);
    }

    @Override
    @Transactional
    public ReviewProviderDocumentResponse reviewProviderDocument(Long adminUserId, Long documentId,
            boolean approved, String notes) {
        ProviderDocument document = providerDocumentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        document.setStatus(approved ? DocumentStatus.APPROVED : DocumentStatus.REJECTED);
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));
        document.setReviewedBy(admin);
        document.setReviewedAt(LocalDateTime.now());
        document = providerDocumentRepository.save(document);

        // Approval of all documents verifies the provider (business rule owned here).
        ProviderProfile provider = document.getProvider();
        boolean allApproved = provider.getDocuments().stream()
                .allMatch(d -> d.getStatus() == DocumentStatus.APPROVED);
        if (allApproved && !provider.getDocuments().isEmpty()) {
            provider.setVerificationStatus(VerificationStatus.VERIFIED);
            providerProfileRepository.save(provider);
        }

        providerProfileRepository.save(provider);
        auditService.record(adminUserId, approved ? "PROVIDER_DOCUMENT_APPROVED" : "PROVIDER_DOCUMENT_REJECTED",
                "document:" + documentId);

        ReviewProviderDocumentResponse response = new ReviewProviderDocumentResponse();
        response.setDocument(providerDocumentMapper.toDto(document));
        response.setReviewedAt(document.getReviewedAt());
        return response;
    }

    @Override
    public void assertOwnership(com.example.serbisyofullstack.model.entity.ProviderProfile provider, Long currentUserId) {
        if (provider == null || provider.getUser() == null
                || !provider.getUser().getUserId().equals(currentUserId)) {
            throw new ForbiddenException("You do not own this provider profile");
        }
    }

    private ProviderProfile requireOwnProfile(Long currentUserId) {
        ProviderProfile profile = providerProfileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("No provider profile for the current user"));
        assertOwnership(profile, currentUserId);
        return profile;
    }
}
