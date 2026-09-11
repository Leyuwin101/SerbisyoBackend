package com.example.serbisyofullstack.service.impl;

import com.example.serbisyofullstack.audit.AuditService;
import com.example.serbisyofullstack.dto.nested.ReportSummaryDto;
import com.example.serbisyofullstack.dto.request.report.CreateReportRequest;
import com.example.serbisyofullstack.dto.response.report.CreateReportResponse;
import com.example.serbisyofullstack.exception.ResourceNotFoundException;
import com.example.serbisyofullstack.mapper.ReportMapper;
import com.example.serbisyofullstack.model.entity.Report;
import com.example.serbisyofullstack.model.entity.User;
import com.example.serbisyofullstack.model.enums.ReportStatus;
import com.example.serbisyofullstack.repository.ReportRepository;
import com.example.serbisyofullstack.repository.UserRepository;
import com.example.serbisyofullstack.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reports: any authenticated user files a report; admins triage and resolve.
 * {@code reportedBy} always comes from the authenticated principal. Every
 * resolution decision is written to the audit trail.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;
    private final AuditService auditService;

    @Override
    @Transactional
    public CreateReportResponse createReport(Long reporterUserId, CreateReportRequest request) {
        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Report report = reportMapper.toEntity(request);
        report.setReportedBy(reporter);
        report.setStatus(ReportStatus.OPEN);
        report = reportRepository.save(report);
        auditService.record(reporterUserId, "REPORT_CREATED", "report", report.getReportId(),
                "entityType=" + report.getEntityType() + ",entityId=" + report.getEntityId());
        CreateReportResponse response = new CreateReportResponse();
        response.setReport(reportMapper.toDto(report));
        response.setCreatedAt(report.getCreatedAt());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public ReportSummaryDto getReport(Long reportId) {
        return reportRepository.findById(reportId).map(reportMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportSummaryDto> listReports(Pageable pageable) {
        return reportRepository.findAll(pageable).map(reportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReportSummaryDto> listOwnReports(Long reporterUserId, Pageable pageable) {
        return reportRepository.findByReportedBy_UserId(reporterUserId, pageable).map(reportMapper::toDto);
    }

    @Override
    @Transactional
    public void resolveReport(Long adminUserId, Long reportId, boolean upheld, String resolution) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        if (report.getStatus() == ReportStatus.RESOLVED || report.getStatus() == ReportStatus.DISMISSED) {
            throw new IllegalStateException("Report is already closed");
        }
        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));
        report.setAssignedTo(admin);
        report.setStatus(upheld ? ReportStatus.RESOLVED : ReportStatus.DISMISSED);
        report.setResolution(resolution);
        report.setResolvedAt(java.time.LocalDateTime.now());
        reportRepository.save(report);
        auditService.record(adminUserId, upheld ? "REPORT_RESOLVED" : "REPORT_DISMISSED",
                "report", reportId, resolution);
    }
}
