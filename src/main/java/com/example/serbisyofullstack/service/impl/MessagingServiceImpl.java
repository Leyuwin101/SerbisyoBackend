package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.dto.nested.ConversationSummaryDto;
import com.example.serbisyofullstack.dto.nested.MessageDto;
import com.example.serbisyofullstack.dto.request.messaging.CreateConversationRequest;
import com.example.serbisyofullstack.dto.request.messaging.MarkMessageReadRequest;
import com.example.serbisyofullstack.dto.request.messaging.SendMessageRequest;
import com.example.serbisyofullstack.dto.response.messaging.CreateConversationResponse;
import com.example.serbisyofullstack.dto.response.messaging.MarkMessageReadResponse;
import com.example.serbisyofullstack.dto.response.messaging.SendMessageResponse;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.ConversationMapper;
import com.example.serbisyofullstack.mapper.MessageMapper;
import com.example.serbisyofullstack.model.entity.Conversation;
import com.example.serbisyofullstack.model.entity.CustomerProfile;
import com.example.serbisyofullstack.model.entity.Message;
import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.repository.ConversationRepository;
import com.example.serbisyofullstack.repository.CustomerProfileRepository;
import com.example.serbisyofullstack.repository.MessageRepository;
import com.example.serbisyofullstack.repository.ProviderProfileRepository;
import com.example.serbisyofullstack.service.MessagingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Messaging business operations: conversation deduplication, message
 * authorization (only the two conversation participants may post), read
 * receipts. Sender identity is always resolved from the authenticated user —
 * never taken from the request body.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessagingServiceImpl implements MessagingService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final com.example.serbisyofullstack.repository.UserRepository userRepository;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public CreateConversationResponse createConversation(Long currentUserId, CreateConversationRequest request) {
        // The customer side is always the authenticated user's customer profile;
        // the request only names the provider. A user cannot open a conversation
        // "as" someone else.
        CustomerProfile customer = customerProfileRepository.findByUserId(currentUserId)
                .orElseThrow(() -> new ForbiddenException("Only customers can start a conversation"));
        ProviderProfile provider = providerProfileRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        Conversation conversation = conversationRepository
                .findByCustomerCustomerProfileIdAndProviderProviderProfileId(
                        customer.getCustomerProfileId(), provider.getProviderProfileId())
                .orElseGet(() -> conversationRepository.save(buildConversation(customer, provider)));

        CreateConversationResponse response = new CreateConversationResponse();
        ConversationSummaryDto summary = conversationMapper.toSummaryDto(conversation);
        response.setConversation(summary);
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConversationSummaryDto> listConversations(Long currentUserId, Pageable pageable) {
        CustomerProfile customer = customerProfileRepository.findByUserId(currentUserId).orElse(null);
        if (customer != null) {
            return conversationRepository.findByCustomerId(customer.getCustomerProfileId(), pageable)
                    .map(conversationMapper::toSummaryDto);
        }
        ProviderProfile provider = providerProfileRepository.findByUserId(currentUserId).orElse(null);
        if (provider != null) {
            return conversationRepository.findByProviderId(provider.getProviderProfileId(), pageable)
                    .map(conversationMapper::toSummaryDto);
        }
        return Page.empty(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageDto> listMessages(Long currentUserId, Long conversationId, Pageable pageable) {
        getAuthorizedConversation(currentUserId, conversationId);
        return messageRepository.findByConversationIdOrderBySentAtAsc(conversationId, pageable)
                .map(messageMapper::toDto);
    }

    @Override
    @Transactional
    public SendMessageResponse sendMessage(Long currentUserId, Long conversationId, SendMessageRequest request) {
        Conversation conversation = getAuthorizedConversation(currentUserId, conversationId);
        if (request.getContent() == null || request.getContent().isBlank()) {
            throw new ValidationException("Message content is required");
        }

        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(userRepositoryReference(currentUserId));
        message.setContent(request.getContent());
        message.setAttachmentReference(request.getAttachmentReference());
        message = messageRepository.save(message);

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        SendMessageResponse response = new SendMessageResponse();
        response.setMessage(messageMapper.toDto(message));
        response.setSentAt(message.getSentAt());
        return response;
    }

    @Override
    @Transactional
    public MarkMessageReadResponse markMessageRead(Long currentUserId, MarkMessageReadRequest request) {
        LocalDateTime readAt = LocalDateTime.now();
        List<Long> marked = new ArrayList<>();
        for (Long messageId : request.getMessageIds()) {
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new ResourceNotFoundException("Message not found: " + messageId));
            getAuthorizedConversation(currentUserId, message.getConversation().getConversationId());
            if (message.getReadAt() == null) {
                message.setReadAt(readAt);
                messageRepository.save(message);
            }
            marked.add(messageId);
        }
        MarkMessageReadResponse response = new MarkMessageReadResponse();
        response.setMessageIds(marked);
        response.setReadAt(readAt);
        return response;
    }

    private Conversation buildConversation(CustomerProfile customer, ProviderProfile provider) {
        Conversation conversation = new Conversation();
        conversation.setCustomer(customer);
        conversation.setProvider(provider);
        return conversation;
    }

    private Conversation getAuthorizedConversation(Long currentUserId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
        boolean participant
                = conversation.getCustomer().getUser().getUserId().equals(currentUserId)
                || conversation.getProvider().getUser().getUserId().equals(currentUserId);
        if (!participant) {
            throw new ForbiddenException("You are not a participant of this conversation");
        }
        return conversation;
    }

    private User userRepositoryReference(Long userId) {
        return userRepository.getReferenceById(userId);
    }
}
