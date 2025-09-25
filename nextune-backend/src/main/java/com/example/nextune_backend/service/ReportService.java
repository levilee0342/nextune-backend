package com.example.nextune_backend.service;


import com.example.nextune_backend.dto.request.ReportRequest;
import com.example.nextune_backend.dto.request.ReportUpdateRequest;
import com.example.nextune_backend.dto.response.ReportResponse;
import com.example.nextune_backend.entity.enums.ReportStatus;

import java.util.List;

public interface ReportService {
    ReportResponse createReport(ReportRequest request);
    ReportResponse updateReport(String reportId, ReportUpdateRequest request);
    ReportResponse updateStatus(String reportId, ReportStatus status);
    void deleteReport(String reportId);

    ReportResponse getReportById(String reportId);
    List<ReportResponse> getReportsByUser(String userId);
    List<ReportResponse> getReportsByEntity(String entityId);
    List<ReportResponse> getReportsByStatus(ReportStatus status);
    List<ReportResponse> getReportsByType(String reportType);
    List<ReportResponse> getAllReports();
}
