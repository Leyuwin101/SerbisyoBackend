package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.BookingItemDto;
import com.example.serbisyofullstack.dto.request.booking.BookingItemRequest;
import com.example.serbisyofullstack.model.entity.BookingItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Maps between {@link BookingItem} and its DTOs. Booking items have no
 * separate update flow — they are replaced wholesale on booking update.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingItemMapper {

    @Mapping(source = "bookingItemId", target = "id")
    @Mapping(source = "booking.bookingId", target = "bookingId")
    @Mapping(source = "service.serviceId", target = "serviceId")
    BookingItemDto toDto(BookingItem entity);

    List<BookingItemDto> toDtoList(List<BookingItem> entities);

    @Mapping(target = "bookingItemId", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "service", ignore = true)   // resolved from serviceId in the service
    BookingItem toEntity(BookingItemRequest request);

    List<BookingItem> toEntityList(List<BookingItemRequest> requests);
}
