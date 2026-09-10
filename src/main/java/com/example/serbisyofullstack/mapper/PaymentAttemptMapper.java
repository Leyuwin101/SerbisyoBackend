package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.PaymentAttemptDto;
import com.example.serbisyofullstack.model.entity.PaymentAttempt;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps {@link PaymentAttempt} to {@link PaymentAttemptDto}. Attempts are
 * recorded by the webhook processor, so only entity-to-DTO mapping exists.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentAttemptMapper {

    @Mapping(source = "paymentAttemptId", target = "paymentAttemptId")
    @Mapping(source = "payment.paymentId", target = "paymentId")
    PaymentAttemptDto toDto(PaymentAttempt entity);
}
