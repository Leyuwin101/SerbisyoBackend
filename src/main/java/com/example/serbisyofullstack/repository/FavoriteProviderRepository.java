package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.FavoriteProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteProviderRepository extends JpaRepository<FavoriteProvider, Long> {

    List<FavoriteProvider> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndProviderId(Long customerId, Long providerId);

    void deleteByCustomerIdAndProviderId(Long customerId, Long providerId);
}
