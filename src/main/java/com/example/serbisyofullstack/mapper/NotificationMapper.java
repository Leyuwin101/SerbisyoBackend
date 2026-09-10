package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.NotificationDto;
import com.example.serbisyofullstack.model.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps {@link Notification} to {@link NotificationDto}. Notifications are
 * system-generated, so there is no request-to-entity mapping.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMapper {

    @Mapping(source = "notificationId", target = "id")
    @Mapping(source = "user.userId", target = "userId")
    @Mapping(target = "read", expression = "java(entity.getReadAt() != null)")
    NotificationDto toDto(Notification entity);
}
