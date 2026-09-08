package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    List<Conversation> findByCustomerId(Long customerId);

    List<Conversation> findByProviderId(Long providerId);

    List<Conversation> findByBookingId(Long bookingId);
}
