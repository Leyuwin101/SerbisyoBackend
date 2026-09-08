package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.ServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceImageRepository extends JpaRepository<ServiceImage, Long> {

    List<ServiceImage> findByServiceIdOrderByIdAsc(Long serviceId);
}
