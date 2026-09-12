package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.ProviderProfile;
import com.example.serbisyofullstack.model.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProviderProfileRepository extends JpaRepository<ProviderProfile, Long> {

    Optional<ProviderProfile> findByUser_UserId(Long userId);

    @Query("SELECT p FROM ProviderProfile p WHERE p.user.userId = :userId")
    Optional<ProviderProfile> findByUserId(@Param("userId") Long userId);

    List<ProviderProfile> findByVerificationStatus(VerificationStatus verificationStatus);
}
