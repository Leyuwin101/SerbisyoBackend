package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.messaging.CreateConversationRequest;
import com.example.serbisyofullstack.dto.request.messaging.MarkMessageReadRequest;
import com.example.serbisyofullstack.dto.request.messaging.SendMessageRequest;
import com.example.serbisyofullstack.dto.nested.ConversationSummaryDto;
import com.example.serbisyofullstack.dto.nested.MessageDto;
import com.example.serbisyofullstack.dto.response.messaging.CreateConversationResponse;
import com.example.serbisyofullstack.dto.response.messaging.MarkMessageReadResponse;
import com.example.serbisyofullstack.dto.response.messaging.SendMessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Messaging: conversations between a customer and a provider, message send/
 * read. Participants can only see their own conversations.
 */
public interface MessagingService {

    CreateConversationResponse createConversation(Long currentUserId, CreateConversationRequest request);

    Page<ConversationSummaryDto> listConversations(Long currentUserId, Pageable pageable);

    SendMessageResponse sendMessage(Long currentUserId, Long conversationId, SendMessageRequest request);

    Page<MessageDto> listMessages(Long currentUserId, Long conversationId, Pageable pageable);

    MarkMessageReadResponse markMessageRead(Long currentUserId, MarkMessageReadRequest request);
}
