package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @org.springframework.data.jpa.repository.Query("select m from Message m where m.conversation.conversationId = :conversationId order by m.sentAt asc")
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);

    @org.springframework.data.jpa.repository.Query("select m from Message m where m.conversation.conversationId = :conversationId order by m.sentAt asc")
    Page<Message> findByConversationIdOrderBySentAtAsc(Long conversationId, Pageable pageable);
}
