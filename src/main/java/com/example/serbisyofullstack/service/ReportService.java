package com.example.serbisyofullstack.service;

import com.example.serbisyofullstack.dto.request.report.CreateReportRequest;
import com.example.serbisyofullstack.dto.nested.ReportSummaryDto;
import com.example.serbisyofullstack.dto.response.report.CreateReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Reports: users report entities (users, services, reviews); admins triage.
 */
public interface ReportService {

    CreateReportResponse createReport(Long reporterUserId, CreateReportRequest request);

    ReportSummaryDto getReport(Long reportId);

    Page<ReportSummaryDto> listReports(Pageable pageable);

    Page<ReportSummaryDto> listOwnReports(Long reporterUserId, Pageable pageable);

    void resolveReport(Long adminUserId, Long reportId, boolean upheld, String resolution);
}
