package com.example.serbisyofullstack.payment;

import com.example.serbisyofullstack.dto.request.payment.PaymentWebhookRequest;
import com.example.serbisyofullstack.model.entity.Payment;
import com.example.serbisyofullstack.model.entity.PaymentAttempt;
import com.example.serbisyofullstack.model.enums.PaymentStatus;
import com.example.serbisyofullstack.repository.PaymentAttemptRepository;
import com.example.serbisyofullstack.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Processes payment gateway webhooks: verifies the payload, updates the
 * matching Payment status and records a PaymentAttempt for tracing.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentWebhookProcessor {

    private final PaymentRepository paymentRepository;
    private final PaymentAttemptRepository paymentAttemptRepository;

    /**
     * Handle a webhook payload. Returns the updated Payment.
     *
     * @throws EntityNotFoundException when no payment matches the reference
     */
    @Transactional
    public Payment process(PaymentWebhookRequest request) {
        Payment payment = paymentRepository.findByProviderReference(request.getProviderReference())
                .orElseThrow(() -> new EntityNotFoundException(
                "No payment found for provider reference " + request.getProviderReference()));

        if (request.getPaymentStatus() != null) {
            payment.setStatus(request.getPaymentStatus());
        }

        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setPayment(payment);
        if (request.getAttemptStatus() != null) {
            attempt.setStatus(request.getAttemptStatus());
        }
        attempt.setGatewayReference(request.getGatewayReference());
        attempt.setFailureReason(request.getFailureReason());
        paymentAttemptRepository.save(attempt);

        log.info("Webhook processed for payment {}: status={}, attempt={}",
                payment.getPaymentId(), payment.getStatus(), attempt.getStatus());

        return payment;
    }
}
