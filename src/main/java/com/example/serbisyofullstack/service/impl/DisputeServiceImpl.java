package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.audit.AuditService;
import com.example.serbisyofullstack.dto.nested.DisputeDto;
import com.example.serbisyofullstack.dto.request.dispute.CreateDisputeRequest;
import com.example.serbisyofullstack.dto.request.dispute.ResolveDisputeRequest;
import com.example.serbisyofullstack.dto.response.dispute.CreateDisputeResponse;
import com.example.serbisyofullstack.dto.response.dispute.ResolveDisputeResponse;
import com.example.serbisyofullstack.exception.ConflictException;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.DisputeMapper;
import com.example.serbisyofullstack.model.entity.Booking;
import com.example.serbisyofullstack.model.entity.Dispute;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.enums.DisputeStatus;
import com.example.serbisyofullstack.repository.BookingRepository;
import com.example.serbisyofullstack.repository.DisputeRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.service.DisputeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dispute business operations. Participants of a booking may open exactly one
 * dispute against it; only administrators resolve or reject disputes, and every
 * resolution is audited.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final DisputeMapper disputeMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public CreateDisputeResponse openDispute(Long currentUserId, CreateDisputeRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Only the customer or the provider involved in the booking may dispute it.
        Long customerUserId = booking.getCustomer().getUser().getUserId();
        Long providerUserId = booking.getProvider().getUser().getUserId();
        if (!customerUserId.equals(currentUserId) && !providerUserId.equals(currentUserId)) {
            throw new ForbiddenException("You can only open disputes for bookings you are involved in");
        }

        // One dispute per booking (enforced again by application rule; the
        // repository query surfaces duplicates for a clean conflict response).
        if (disputeRepository.findByBookingId(booking.getBookingId()).isPresent()) {
            throw new ConflictException("A dispute already exists for this booking");
        }

        Dispute dispute = disputeMapper.toEntity(request);
        dispute.setBooking(booking);
        dispute.setOpenedBy(userRepository.getReferenceById(currentUserId));
        dispute.setStatus(DisputeStatus.OPEN);
        dispute = disputeRepository.save(dispute);

        auditService.record(currentUserId, "DISPUTE_OPENED", "dispute:" + dispute.getDisputeId());
        log.info("Dispute {} opened on booking {} by user {}", dispute.getDisputeId(),
                booking.getBookingId(), currentUserId);

        CreateDisputeResponse response = new CreateDisputeResponse();
        response.setDispute(disputeMapper.toDto(dispute));
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public DisputeDto getDispute(Long currentUserId, Long disputeId) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found"));
        assertParticipantOrAdmin(dispute, currentUserId);
        return disputeMapper.toDto(dispute);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisputeDto> listDisputesForUser(Long currentUserId, Pageable pageable) {
        return disputeRepository.findByOpenedById(currentUserId, pageable)
                .map(disputeMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DisputeDto> listOpenDisputes(Pageable pageable) {
        return disputeRepository.findByStatusIn(
                List.of(DisputeStatus.OPEN, DisputeStatus.UNDER_REVIEW), pageable)
                .map(disputeMapper::toDto);
    }

    @Override
    @Transactional
    public ResolveDisputeResponse resolveDispute(Long adminUserId, Long disputeId, ResolveDisputeRequest request) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispute not found"));
        if (dispute.getStatus() != DisputeStatus.OPEN && dispute.getStatus() != DisputeStatus.UNDER_REVIEW) {
            throw new ValidationException("This dispute has already been closed");
        }

        User admin = userRepository.getReferenceById(adminUserId);
        dispute.setStatus(DisputeStatus.RESOLVED);
        dispute.setResolution(request.getResolution());
        dispute.setResolvedBy(admin);
        dispute.setResolvedAt(LocalDateTime.now());
        dispute = disputeRepository.save(dispute);

        auditService.record(adminUserId, "DISPUTE_RESOLVED", "dispute:" + dispute.getDisputeId());
        log.info("Dispute {} resolved by admin {}", dispute.getDisputeId(), adminUserId);

        ResolveDisputeResponse response = new ResolveDisputeResponse();
        response.setDispute(disputeMapper.toDto(dispute));
        response.setResolvedAt(dispute.getResolvedAt());
        return response;
    }

    private void assertParticipantOrAdmin(Dispute dispute, Long currentUserId) {
        Booking booking = dispute.getBooking();
        Long customerUserId = booking.getCustomer().getUser().getUserId();
        Long providerUserId = booking.getProvider().getUser().getUserId();
        if (customerUserId.equals(currentUserId) || providerUserId.equals(currentUserId)) {
            return;
        }
        // Staff roles reach this through controller-level role checks; without
        // them a non-participant is simply forbidden from viewing the dispute.
        throw new ForbiddenException("You do not have access to this dispute");
    }
}
