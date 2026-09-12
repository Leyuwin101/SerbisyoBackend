package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.ProviderPayoutAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProviderPayoutAccountRepository extends JpaRepository<ProviderPayoutAccount, Long> {

    @org.springframework.data.jpa.repository.Query("select a from ProviderPayoutAccount a where a.provider.providerProfileId = :providerId")
    Optional<ProviderPayoutAccount> findByProviderId(Long providerId);
}
