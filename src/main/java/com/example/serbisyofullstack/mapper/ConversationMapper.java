package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.ConversationSummaryDto;
import com.example.serbisyofullstack.dto.request.messaging.CreateConversationRequest;
import com.example.serbisyofullstack.model.entity.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Conversation} and {@link ConversationSummaryDto}.
 * Last-message fields are service-computed, not mapped from the entity.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConversationMapper {

    @Mapping(source = "conversationId", target = "id")
    @Mapping(source = "customer.customerProfileId", target = "customerId")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    @Mapping(source = "booking.bookingId", target = "bookingId")
    ConversationSummaryDto toDto(Conversation entity);

    /** Alias used by MessagingServiceImpl for conversation listings. */
    default ConversationSummaryDto toSummaryDto(Conversation entity) {
        return toDto(entity);
    }

    @Mapping(target = "conversationId", ignore = true)
    @Mapping(target = "customer", ignore = true)   // from the authenticated principal
    @Mapping(target = "provider", ignore = true)   // resolved by the service
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Conversation toEntity(CreateConversationRequest request);
}
