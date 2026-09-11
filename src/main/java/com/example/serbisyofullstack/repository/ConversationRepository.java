package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Page<Conversation> findByCustomerId(Long customerId, Pageable pageable);

    Page<Conversation> findByProviderId(Long providerId, Pageable pageable);

    List<Conversation> findByBookingId(Long bookingId);

    java.util.Optional<Conversation> findByCustomerCustomerProfileIdAndProviderProviderProfileId(Long customerId, Long providerId);
}
