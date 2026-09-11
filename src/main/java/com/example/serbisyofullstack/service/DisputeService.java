package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.dispute.CreateDisputeRequest;
import com.example.serbisyofullstack.dto.request.dispute.ResolveDisputeRequest;
import com.example.serbisyofullstack.dto.nested.DisputeDto;
import com.example.serbisyofullstack.dto.response.dispute.CreateDisputeResponse;
import com.example.serbisyofullstack.dto.response.dispute.ResolveDisputeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Disputes: opening (customer/provider involved in the booking) and admin
 * resolution. All resolutions are audited.
 */
public interface DisputeService {

    CreateDisputeResponse openDispute(Long currentUserId, CreateDisputeRequest request);

    DisputeDto getDispute(Long currentUserId, Long disputeId);

    Page<DisputeDto> listDisputesForUser(Long currentUserId, Pageable pageable);

    Page<DisputeDto> listOpenDisputes(Pageable pageable);

    ResolveDisputeResponse resolveDispute(Long adminUserId, Long disputeId, ResolveDisputeRequest request);
}
