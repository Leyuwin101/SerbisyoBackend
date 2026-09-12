package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Payout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PayoutRepository extends JpaRepository<Payout, Long>{

    @org.springframework.data.jpa.repository.Query("select p from Payout p where p.provider.providerProfileId = :providerId order by p.createdAt desc")
    List<Payout> findByProviderIdOrderByCreatedAtDest(Long providerId);

}
