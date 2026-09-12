package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.ServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceImageRepository extends JpaRepository<ServiceImage, Long> {

    @Query("SELECT i FROM ServiceImage i WHERE i.service.serviceId = :serviceId ORDER BY i.id ASC")
    List<ServiceImage> findByServiceIdOrderByIdAsc(@Param("serviceId") Long serviceId);
}
