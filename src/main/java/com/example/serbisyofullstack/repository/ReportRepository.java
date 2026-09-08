package com.example.serbisyofullstack.repository;

import com.example.serbisyofullstack.model.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByReportedId(Long reporterId);

    List<Report> findByStatus(String status);
}
