package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {

    @org.springframework.data.jpa.repository.Query("select a from PaymentAttempt a where a.payment.paymentId = :paymentId order by a.attemptedAt desc")
    List<PaymentAttempt> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);
}
