package com.example.nextune_backend.exception.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.Instant;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private Instant timestamp = Instant.now();
    private int status;
    private String error;
    private String message;
    private String path;
    private String exceptionMessage;
    private String traceId;


    public ApiError(HttpStatus status, String message, String path) {
        this.status = status.value();
        this.message = message;
        this.path = path;
    }
}

