package com.example.nextune_backend.service.impl;


import com.example.nextune_backend.dto.request.ReportRequest;
import com.example.nextune_backend.dto.request.ReportUpdateRequest;
import com.example.nextune_backend.dto.response.ReportResponse;
import com.example.nextune_backend.entity.Report;
import com.example.nextune_backend.entity.enums.ReportStatus;

import com.example.nextune_backend.entity.User;

import com.example.nextune_backend.mapper.ReportMapper;
import com.example.nextune_backend.repository.ReportRepository;
import com.example.nextune_backend.repository.UserRepository;
import com.example.nextune_backend.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;

    @Override
    public ReportResponse createReport(ReportRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Report report = reportMapper.toEntity(request, user);
        report.setStatus(ReportStatus.PENDING);
        reportRepository.save(report);
        return reportMapper.toResponse(report);
    }

    @Override
    public ReportResponse updateReport(String reportId, ReportUpdateRequest request) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportMapper.updateFromRequest(request, report);
        reportRepository.save(report);
        return reportMapper.toResponse(report);
    }

    @Override
    public ReportResponse updateStatus(String reportId, ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setStatus(status);
        reportRepository.save(report);
        return reportMapper.toResponse(report);
    }

    @Override
    public void deleteReport(String reportId) {
        if (!reportRepository.existsById(reportId)) {
            throw new RuntimeException("Report not found");
        }
        reportRepository.deleteById(reportId);
    }

    @Override
    public ReportResponse getReportById(String reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return reportMapper.toResponse(report);
    }

    @Override
    public List<ReportResponse> getReportsByUser(String userId) {
        return reportMapper.toResponseList(reportRepository.findByUser_Id(userId));
    }

    @Override
    public List<ReportResponse> getReportsByEntity(String entityId) {
        return reportMapper.toResponseList(reportRepository.findByEntityId(entityId));
    }

    @Override
    public List<ReportResponse> getReportsByStatus(ReportStatus status) {
        return reportMapper.toResponseList(reportRepository.findByStatus(status));
    }

    @Override
    public List<ReportResponse> getReportsByType(String reportType) {
        return reportMapper.toResponseList(reportRepository.findByReportType(reportType));
    }

    @Override
    public List<ReportResponse> getAllReports() {
        return reportMapper.toResponseList(reportRepository.findAll());
    }
}
