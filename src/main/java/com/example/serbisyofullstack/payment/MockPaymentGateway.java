package com.example.serbisyofullstack.payment;

import com.example.serbisyofullstack.model.entity.Payment;
import com.example.serbisyofullstack.model.enums.PaymentMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Mock gateway adapter. Accepts every payment method and returns a synthetic
 * reference. Replace with real gateway adapters (PayMongo, Stripe, ...) that
 * implement {@link PaymentGateway}.
 */
@Slf4j
@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public boolean supports(PaymentMethod method) {
        return true;
    }

    @Override
    public String createCharge(Payment payment) {
        String reference = "MOCK-" + System.currentTimeMillis();
        log.info("[MOCK GATEWAY] charge created: amount={} {} method={} reference={}",
                payment.getAmount(), payment.getCurrency(), payment.getMethod(), reference);
        return reference;
    }

    @Override
    public String refund(String providerReference, BigDecimal amount) {
        String refundReference = "MOCK-RF-" + System.currentTimeMillis();
        log.info("[MOCK GATEWAY] refund: charge={} amount={} reference={}",
                providerReference, amount, refundReference);
        return refundReference;
    }
}
