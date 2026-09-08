package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.AvailabilityException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityExceptionRepository extends JpaRepository<AvailabilityException, Long> {

    List<AvailabilityException> findByProviderIdAndDateBetween(Long providerId, LocalDate startDate, LocalDate endDate);
}
