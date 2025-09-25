package com.example.nextune_backend.repository;

import com.example.nextune_backend.entity.Report;
import com.example.nextune_backend.entity.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, String> {
    List<Report> findByUser_Id(String userId);
    List<Report> findByEntityId(String entityId);
    List<Report> findByStatus(ReportStatus status);
    List<Report> findByReportType(String reportType);
}
