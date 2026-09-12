package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.AvailabilitySchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityScheduleRepository extends JpaRepository<AvailabilitySchedule, Long> {

    @org.springframework.data.jpa.repository.Query(
            "select s from AvailabilitySchedule s where s.provider.providerProfileId = :providerId and s.active = true")
    List<AvailabilitySchedule> findByProviderIdAndActiveTrue(Long providerId);
}
