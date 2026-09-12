package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service,Long> {

    @Query("select s from Service s where s.provider.providerProfileId = :providerId")
    List<Service> findByProviderId(@Param("providerId") Long providerId);

    @Query("select s from Service s where s.category.categoryId = :categoryId")
    List<Service> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("select s from Service s where s.provider.providerProfileId = :providerId and s.active = true")
    List<Service> findByProviderIdAndActiveTrue(@Param("providerId") Long providerId);

    @Query("select s from Service s where s.category.categoryId = :categoryId and s.active = true")
    List<Service> findByCategoryIdAndActiveTrue(@Param("categoryId") Long categoryId);

    @Query("select s from Service s where s.provider.providerProfileId = :providerId")
    Page<Service> findByProviderId(@Param("providerId") Long providerId, Pageable pageable);

    @Query("select s from Service s where s.category.categoryId = :categoryId")
    Page<Service> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // Public browse/search: only active services are visible to the public.
    Page<Service> findByActiveTrue(Pageable pageable);

    Page<Service> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

}
