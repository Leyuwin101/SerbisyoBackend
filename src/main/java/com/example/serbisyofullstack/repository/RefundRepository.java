package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RefundRepository extends JpaRepository<Refund, Long> {

    @org.springframework.data.jpa.repository.Query("select r from Refund r where r.payment.paymentId = :paymentId")
    List<Refund> findByPaymentId(Long paymentId);
}
