package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Dispute;
import com.example.serbisyofullstack.model.enums.DisputeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    Optional<Dispute> findByBookingId(Long bookingId);
    Page<Dispute> findByOpenedById(Long userId, Pageable pageable);
    Page<Dispute> findByStatus(DisputeStatus status, Pageable pageable);
    Page<Dispute> findByStatusIn(List<DisputeStatus> statuses, Pageable pageable);
}
