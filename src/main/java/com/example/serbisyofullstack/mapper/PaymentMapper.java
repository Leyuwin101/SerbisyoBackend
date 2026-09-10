package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.PaymentDto;
import com.example.serbisyofullstack.dto.request.payment.CreatePaymentRequest;
import com.example.serbisyofullstack.dto.request.payment.PaymentWebhookRequest;
import com.example.serbisyofullstack.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Maps between {@link Payment} and its DTOs. Webhook status updates go
 * through {@link #toUpdate(PaymentWebhookRequest, Payment)}.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper extends BaseMapper<Payment, CreatePaymentRequest, PaymentWebhookRequest, PaymentDto> {

    @Override
    @Mapping(source = "paymentId", target = "id")
    @Mapping(source = "booking.bookingId", target = "bookingId")
    PaymentDto toDto(Payment entity);

    @Override
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "booking", ignore = true)      // resolved from bookingId in the service
    @Mapping(target = "status", ignore = true)       // starts as PENDING
    @Mapping(target = "currency", ignore = true)     // derived from the quote in the service
    @Mapping(target = "amount", ignore = true)       // taken from the accepted quote — never the client
    @Mapping(target = "providerReference", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toEntity(CreatePaymentRequest request);

    @Override
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "booking", ignore = true)
    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "currency", ignore = true)
    @Mapping(target = "method", ignore = true)
    @Mapping(target = "providerReference", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toUpdate(PaymentWebhookRequest request, @MappingTarget Payment entity);
}
