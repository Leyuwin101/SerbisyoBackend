package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {

    Optional<Dispute> findByBookingId(Long bookingId);

    List<Dispute> findByOpenedBy(Long userId);

    List<Dispute> findByStatus(String status);
}
