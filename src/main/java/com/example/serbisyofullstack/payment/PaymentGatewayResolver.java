package com.example.serbisyofullstack.payment;

import com.example.serbisyofullstack.model.enums.PaymentMethod;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Picks the {@link PaymentGateway} adapter that supports a given
 * {@link PaymentMethod}. Throws when no adapter is registered for it.
 */
@Component
public class PaymentGatewayResolver {

    private final List<PaymentGateway> gateways;

    public PaymentGatewayResolver(List<PaymentGateway> gateways) {
        this.gateways = gateways;
    }

    public PaymentGateway resolve(PaymentMethod method) {
        return gateways.stream()
                .filter(g -> g.supports(method))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                "No payment gateway registered for method " + method));
    }
}
