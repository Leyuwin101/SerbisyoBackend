package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {

    Optional<ProviderProfile> findByUserId(Long userId);

    List<ProviderProfile> findByVerificationStatus(VerificationStatus verificationStatus);
}
