package com.example.serbisyofullstack.payment;

import com.example.serbisyofullstack.model.entity.Payment;
import com.example.serbisyofullstack.model.enums.PaymentMethod;
import com.example.serbisyofullstack.model.enums.PaymentStatus;

import java.math.BigDecimal;

/**
 * Abstraction over a payment gateway (PayMongo, Stripe, GCash, ...). Each
 * gateway plugs in as a Spring bean implementing this interface; the
 * {@link PaymentGatewayResolver} picks the right adapter by
 * {@link PaymentMethod}.
 */
public interface PaymentGateway {

    /**
     * @return true when this gateway handles the given payment method.
     */
    boolean supports(PaymentMethod method);

    /**
     * Create a charge/intent at the gateway for the given payment.
     *
     * @return a gateway reference (transaction / intent id) once accepted.
     */
    String createCharge(Payment payment);

    /**
     * Refund an earlier charge at the gateway.
     *
     * @param providerReference the gateway reference returned by
     * {@link #createCharge}
     * @param amount amount to refund (may be partial)
     * @return the gateway's refund reference
     */
    String refund(String providerReference, BigDecimal amount);
}
