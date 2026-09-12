package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @org.springframework.data.jpa.repository.Query("select c from Conversation c where c.customer.customerProfileId = :customerId")
    Page<Conversation> findByCustomerId(Long customerId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("select c from Conversation c where c.provider.providerProfileId = :providerId")
    Page<Conversation> findByProviderId(Long providerId, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("select c from Conversation c where c.booking.bookingId = :bookingId")
    List<Conversation> findByBookingId(Long bookingId);

    java.util.Optional<Conversation> findByCustomerCustomerProfileIdAndProviderProviderProfileId(Long customerId, Long providerId);
}
