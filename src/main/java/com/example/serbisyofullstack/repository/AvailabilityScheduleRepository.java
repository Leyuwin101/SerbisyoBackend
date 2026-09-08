package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.AvailabilitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilitySchedule, Long> {

    List<AvailabilitySchedule> findByProviderIdAndActiveTrue(Long providerId);
}
