package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.payment.CreatePaymentRequest;
import com.example.serbisyofullstack.dto.request.payment.CreateRefundRequest;
import com.example.serbisyofullstack.dto.nested.PaymentDto;
import com.example.serbisyofullstack.dto.nested.RefundDto;
import com.example.serbisyofullstack.dto.response.payment.CreatePaymentResponse;
import com.example.serbisyofullstack.dto.response.payment.CreateRefundResponse;

/**
 * Payment business operations. Gateway communication stays behind
 * {@code PaymentGateway}; this service owns amounts (BigDecimal, from the
 * accepted quote — never the client), state and idempotency.
 */
public interface PaymentService {

    CreatePaymentResponse createPayment(Long customerUserId, CreatePaymentRequest request);

    PaymentDto getPaymentForUser(Long currentUserId, Long paymentId);

    CreateRefundResponse requestRefund(Long customerUserId, Long paymentId, CreateRefundRequest request);

    RefundDto getRefund(Long currentUserId, Long refundId);
}
