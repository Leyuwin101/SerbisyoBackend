package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.BookingSummaryDto;
import com.example.serbisyofullstack.dto.request.booking.CreateBookingRequest;
import com.example.serbisyofullstack.dto.request.booking.UpdateBookingRequest;
import com.example.serbisyofullstack.model.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Booking} and its DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookingMapper extends BaseMapper<Booking, CreateBookingRequest, UpdateBookingRequest, BookingSummaryDto> {

    @Override
    @Mapping(source = "bookingId", target = "id")
    @Mapping(source = "customer.customerProfileId", target = "customerId")
    @Mapping(source = "provider.providerProfileId", target = "providerId")
    @Mapping(source = "service.serviceId", target = "serviceId")
    @Mapping(source = "scheduled_start", target = "scheduledStart")
    @Mapping(target = "status", expression = "java(entity.getStatus() != null ? entity.getStatus().name() : null)")
    BookingSummaryDto toDto(Booking entity);

    @Override
    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "customer", ignore = true)          // resolved from the authenticated principal
    @Mapping(target = "provider", ignore = true)          // resolved from providerId in the service
    @Mapping(target = "service", ignore = true)           // resolved from serviceId in the service
    @Mapping(target = "address", ignore = true)           // resolved from addressId in the service
    @Mapping(target = "status", ignore = true)            // starts as PENDING
    @Mapping(target = "quotedAmount", ignore = true)      // set when the provider quotes
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "scheduled_start", source = "scheduledStart")
    Booking toEntity(CreateBookingRequest request);

    @Override
    @Mapping(target = "bookingId", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "provider", ignore = true)
    @Mapping(target = "service", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "quotedAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "scheduled_start", source = "scheduledStart")
    Booking toUpdate(UpdateBookingRequest request, @MappingTarget Booking entity);

    /**
     * Requests carry zoned datetimes; the entity stores local datetimes.
     * Zone conversion (to the provider's timezone) is a service concern.
     */
    default java.time.LocalDateTime map(java.time.OffsetDateTime value) {
        return value != null ? value.toLocalDateTime() : null;
    }
}
