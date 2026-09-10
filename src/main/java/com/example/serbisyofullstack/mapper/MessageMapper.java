package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.MessageDto;
import com.example.serbisyofullstack.dto.request.messaging.SendMessageRequest;
import com.example.serbisyofullstack.model.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Message} and {@link MessageDto}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MessageMapper {

    @Mapping(source = "messageId", target = "id")
    @Mapping(source = "conversation.conversationId", target = "conversationId")
    @Mapping(source = "sender.userId", target = "senderId")
    MessageDto toDto(Message entity);

    @Mapping(target = "messageId", ignore = true)
    @Mapping(target = "conversation", ignore = true)   // resolved by the service
    @Mapping(target = "sender", ignore = true)         // from the authenticated principal
    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "readAt", ignore = true)
    Message toEntity(SendMessageRequest request);
}
