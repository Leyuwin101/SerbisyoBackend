package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Report;
import com.example.serbisyofullstack.model.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r FROM Report r WHERE r.reportedBy.userId = :reporterId")
    List<Report> findByReportedId(@Param("reporterId") Long reporterId);

    @Query("SELECT r FROM Report r WHERE r.status = :status")
    List<Report> findByStatus(@Param("status") ReportStatus status);

    Page<Report> findByReportedBy_UserId(Long reportedByUserId, Pageable pageable);
}
