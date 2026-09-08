package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.PaymentAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentAttemptRepository extends JpaRepository<PaymentAttempt, Long> {

    List<PaymentAttempt> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);
}
