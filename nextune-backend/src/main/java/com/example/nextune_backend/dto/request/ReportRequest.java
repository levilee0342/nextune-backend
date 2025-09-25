package com.example.nextune_backend.dto.request;

import lombok.Data;

@Data
public class ReportRequest {
    private String userId;
    private String content;
    private String entityId;
    private String reportType;
}