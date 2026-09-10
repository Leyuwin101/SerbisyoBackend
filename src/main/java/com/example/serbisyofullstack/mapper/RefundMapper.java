package com.example.serbisyofullstack.mapper;

import com.example.serbisyofullstack.dto.nested.RefundDto;
import com.example.serbisyofullstack.dto.request.payment.CreateRefundRequest;
import com.example.serbisyofullstack.model.entity.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps {@link Refund} to {@link RefundDto} and create-requests onto new
 * refunds. Refund amount/status/providerReference are service-computed.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RefundMapper {

    @Mapping(source = "refundId", target = "refundId")
    @Mapping(source = "payment.paymentId", target = "paymentId")
    RefundDto toDto(Refund entity);

    @Mapping(target = "refundId", ignore = true)
    @Mapping(target = "payment", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "providerReference", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Refund toEntity(CreateRefundRequest request);
}
