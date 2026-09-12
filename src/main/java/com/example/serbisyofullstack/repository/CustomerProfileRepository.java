package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, Long> {

    Optional<CustomerProfile> findByUser_UserId(Long userId);

    @Query("SELECT c FROM CustomerProfile c WHERE c.user.userId = :userId")
    Optional<CustomerProfile> findByUserId(@Param("userId") Long userId);

}
