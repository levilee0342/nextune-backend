package com.example.nextune_backend.controller;


import com.example.nextune_backend.dto.request.ReportRequest;
import com.example.nextune_backend.dto.request.ReportStatusUpdateRequest;
import com.example.nextune_backend.dto.request.ReportUpdateRequest;
import com.example.nextune_backend.dto.response.ReportResponse;
import com.example.nextune_backend.entity.enums.ReportStatus;
import com.example.nextune_backend.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<ReportResponse> createReport(@Valid @RequestBody ReportRequest request) {
        return ResponseEntity.ok(reportService.createReport(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportResponse> updateReport(@PathVariable String id,
                                                       @Valid @RequestBody ReportUpdateRequest request) {
        return ResponseEntity.ok(reportService.updateReport(id, request));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReportResponse> updateStatus(@PathVariable String id,
                                                       @Valid @RequestBody ReportStatusUpdateRequest request) {
        return ResponseEntity.ok(reportService.updateStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReportById(@PathVariable String id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReportResponse>> getReportsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(reportService.getReportsByUser(userId));
    }

    @GetMapping("/entity/{entityId}")
    public ResponseEntity<List<ReportResponse>> getReportsByEntity(@PathVariable String entityId) {
        return ResponseEntity.ok(reportService.getReportsByEntity(entityId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReportResponse>> getReportsByStatus(@PathVariable ReportStatus status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status));
    }

    @GetMapping("/type/{reportType}")
    public ResponseEntity<List<ReportResponse>> getReportsByType(@PathVariable String reportType) {
        return ResponseEntity.ok(reportService.getReportsByType(reportType));
    }
}
