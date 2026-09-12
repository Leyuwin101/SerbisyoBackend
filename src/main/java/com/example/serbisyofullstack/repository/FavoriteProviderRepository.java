package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.FavoriteProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteProviderRepository extends JpaRepository<FavoriteProvider, Long> {

    @org.springframework.data.jpa.repository.Query("select f from FavoriteProvider f where f.customer.customerProfileId = :customerId")
    List<FavoriteProvider> findByCustomerId(Long customerId);

    @org.springframework.data.jpa.repository.Query(
            "select count(f) > 0 from FavoriteProvider f where f.customer.customerProfileId = :customerId and f.provider.providerProfileId = :providerId")
    boolean existsByCustomerIdAndProviderId(Long customerId, Long providerId);

    @org.springframework.data.jpa.repository.Query(
            "delete from FavoriteProvider f where f.customer.customerProfileId = :customerId and f.provider.providerProfileId = :providerId")
    void deleteByCustomerIdAndProviderId(Long customerId, Long providerId);
}
