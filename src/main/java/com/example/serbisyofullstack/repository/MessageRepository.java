package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);

    Page<Message> findByConversationIdOrderBySentAtAsc(Long conversationId, Pageable pageable);
}
