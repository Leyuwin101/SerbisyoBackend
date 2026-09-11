package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service,Long> {

    List<Service> findByProviderId(Long providerId);

    List<Service> findByCategoryId(Long categoryId);

    List<Service> findByProviderIdAndActiveTrue(Long providerId);

    List<Service> findByCategoryIdAndActiveTrue(Long categoryId);

    Page<Service> findByProviderId(Long providerId, Pageable pageable);

    Page<Service> findByCategoryId(Long categoryId, Pageable pageable);

}
