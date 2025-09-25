package com.example.nextune_backend.dto.response;

import com.example.nextune_backend.entity.enums.ReportStatus;
import lombok.Data;

import java.util.Date;

@Data
public class ReportResponse {
    private String id;
    private String userId;
    private String content;
    private Date createdDate;
    private ReportStatus status;
    private String entityId;
    private String reportType;
}
