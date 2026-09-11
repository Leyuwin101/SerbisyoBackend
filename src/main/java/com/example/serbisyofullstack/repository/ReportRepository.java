package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReportedId(Long reporterId);

    List<Report> findByStatus(String status);

    Page<Report> findByReportedBy_UserId(Long reportedByUserId, Pageable pageable);
}
