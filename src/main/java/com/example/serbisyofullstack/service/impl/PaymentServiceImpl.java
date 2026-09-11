package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.audit.AuditService;
import com.example.serbisyofullstack.dto.nested.PaymentDto;
import com.example.serbisyofullstack.dto.nested.RefundDto;
import com.example.serbisyofullstack.dto.request.payment.CreatePaymentRequest;
import com.example.serbisyofullstack.dto.request.payment.CreateRefundRequest;
import com.example.serbisyofullstack.dto.response.payment.CreatePaymentResponse;
import com.example.serbisyofullstack.dto.response.payment.CreateRefundResponse;
import com.example.serbisyofullstack.exception.ForbiddenException;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.exception.ValidationException;
import com.example.serbisyofullstack.mapper.PaymentMapper;
import com.example.serbisyofullstack.mapper.RefundMapper;
import com.example.serbisyofullstack.model.entity.Booking;
import com.example.serbisyofullstack.model.entity.Payment;
import com.example.serbisyofullstack.model.entity.Quote;
import com.example.serbisyofullstack.model.entity.Refund;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.enums.BookingStatus;
import com.example.serbisyofullstack.model.enums.PaymentStatus;
import com.example.serbisyofullstack.model.enums.RefundStatus;
import com.example.serbisyofullstack.repository.BookingRepository;
import com.example.serbisyofullstack.repository.PaymentRepository;
import com.example.serbisyofullstack.repository.RefundRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.service.PaymentService;
import com.example.serbisyofullstack.payment.PaymentGateway;
import com.example.serbisyofullstack.payment.PaymentGatewayResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment business operations. Gateway communication stays behind
 * {@code PaymentGateway}; this service owns amounts (BigDecimal, taken from the
 * accepted quote — never the client), state and idempotency. One payment per
 * booking (unique constraint) is re-used when a retry comes in.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PaymentGatewayResolver gatewayResolver;
    private final PaymentMapper paymentMapper;
    private final RefundMapper refundMapper;
    private final com.example.serbisyofullstack.audit.AuditService auditService;

    @Override
    @Transactional
    public CreatePaymentResponse createPayment(Long customerUserId, CreatePaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!booking.getCustomer().getUser().getUserId().equals(customerUserId)) {
            throw new ForbiddenException("You can only pay for your own bookings");
        }
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ValidationException("Only accepted bookings can be paid");
        }

        // Idempotency: one payment row per booking; retries return it.
        Payment payment = booking.getPayment();
        if (payment == null) {
            Quote quote = booking.getQuote();
            BigDecimal amount = quote != null && quote.getAmount() != null
                    ? quote.getAmount()
                    : booking.getQuotedAmount();
            if (amount == null) {
                throw new ValidationException("Booking has no quote to pay");
            }

            payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(amount);
            payment.setCurrency("PHP");
            payment.setStatus(PaymentStatus.PENDING);
            payment.setMethod(request.getMethod());
            payment.setProviderReference("PAY-" + UUID.randomUUID());
            payment = paymentRepository.save(payment);
        } else if (payment.getStatus() == PaymentStatus.PAID) {
            throw new ValidationException("This booking is already paid");
        }

        // Charge via the gateway adapter chosen by method. External call is
        // short and before any other transactional work in this method.
        PaymentGateway gateway = gatewayResolver.resolve(request.getMethod());
        String gatewayReference = gateway.createCharge(payment);
        if (payment.getProviderReference() == null || payment.getProviderReference().isBlank()) {
            payment.setProviderReference(gatewayReference);
            payment = paymentRepository.save(payment);
        }

        auditService.record(customerUserId, "PAYMENT_INITIATED",
                "payment:" + payment.getPaymentId());

        CreatePaymentResponse response = new CreatePaymentResponse();
        response.setPayment(paymentMapper.toDto(payment));
        response.setCheckoutUrl("/api/v1/payments/mock-checkout/" + payment.getPaymentId());
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPaymentForUser(Long currentUserId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        assertParticipant(payment.getBooking(), currentUserId);
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public CreateRefundResponse requestRefund(Long customerUserId, Long paymentId, CreateRefundRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        assertParticipant(payment.getBooking(), customerUserId);
        if (payment.getStatus() != PaymentStatus.PAID && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {
            throw new ValidationException("Only paid bookings can be refunded");
        }
        BigDecimal alreadyRefunded = refundRepository.findByPaymentId(paymentId).stream()
                .filter(r -> r.getStatus() == RefundStatus.COMPLETED)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (alreadyRefunded.compareTo(payment.getAmount()) >= 0) {
            throw new ValidationException("Payment is already fully refunded");
        }

        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setReason(request.getReason());
        refund.setStatus(RefundStatus.PENDING);
        // Full refund of remaining amount; partial refunds are admin-driven.
        refund.setAmount(payment.getAmount().subtract(alreadyRefunded));
        refund = refundRepository.save(refund);

        PaymentGateway gateway = gatewayResolver.resolve(payment.getMethod());
        String refundReference = gateway.refund(payment.getProviderReference(), refund.getAmount());
        refund.setProviderReference(refundReference);
        refund.setStatus(RefundStatus.COMPLETED);
        refund = refundRepository.save(refund);

        payment.setStatus(alreadyRefunded.add(refund.getAmount()).compareTo(payment.getAmount()) == 0
                ? PaymentStatus.REFUNDED
                : PaymentStatus.PARTIALLY_REFUNDED);
        paymentRepository.save(payment);

        auditService.record(customerUserId, "REFUND_REQUESTED", "refund:" + refund.getRefundId());

        CreateRefundResponse response = new CreateRefundResponse();
        response.setRefund(refundMapper.toDto(refund));
        response.setCreatedAt(LocalDateTime.now());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public RefundDto getRefund(Long currentUserId, Long refundId) {
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));
        assertParticipant(refund.getPayment().getBooking(), currentUserId);
        return refundMapper.toDto(refund);
    }

    private void assertParticipant(Booking booking, Long currentUserId) {
        Long customerUserId = booking.getCustomer().getUser().getUserId();
        Long providerUserId = booking.getProvider().getUser().getUserId();
        if (!customerUserId.equals(currentUserId) && !providerUserId.equals(currentUserId)) {
            throw new ForbiddenException("You do not have access to this payment");
        }
    }
}
