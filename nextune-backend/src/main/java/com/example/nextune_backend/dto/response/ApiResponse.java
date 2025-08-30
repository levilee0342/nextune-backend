package com.example.nextune_backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private T result;
    private String message;
    private int status;
    private boolean success;
}
