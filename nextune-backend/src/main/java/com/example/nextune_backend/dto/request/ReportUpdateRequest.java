package com.example.nextune_backend.dto.request;



import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportUpdateRequest {
    private String content;
    private String reportType;
}
