package com.example.nextune_backend.dto.request;


import com.example.nextune_backend.entity.enums.ReportStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportStatusUpdateRequest {
    @NotNull
    private ReportStatus status;
}
