package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, Long>{

    List<Payout> findByProviderIdOrderByCreatedAtDest(Long providerId);

}
