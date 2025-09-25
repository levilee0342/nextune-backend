package com.example.nextune_backend.exception.handler;

import com.example.nextune_backend.exception.error.ApiError;
import com.example.nextune_backend.exception.error.TrackNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.slf4j.MDC;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${app.env:prod}") // đặt app.env=dev khi chạy local để hiện chi tiết
    private String appEnv;

    private boolean isDev() { return "dev".equalsIgnoreCase(appEnv); }

    private String traceId() { return MDC.get("traceId"); }

    private ApiError buildApiError(HttpStatus status, String message, String path, Throwable ex) {
        ApiError err = new ApiError(status, message, path);
        // Bổ sung thông tin để debug nhanh (ẩn ở prod)
        if (isDev() && ex != null) {
            err.setTimestamp(Instant.now());
            err.setError(ex.getClass().getSimpleName());
            err.setExceptionMessage(ex.getMessage());
        }
        err.setTraceId(traceId());
        return err;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException ex) {
        HttpStatusCode code = ex.getStatusCode();
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(code, ex.getReason());
        pd.setType(URI.create("https://example.com/errors/" + code.value()));
        pd.setTitle(code.is5xxServerError() ? "Server Error" : "Client Error");
        pd.setProperty("traceId", traceId());
        if (isDev()) {
            pd.setProperty("exception", ex.getClass().getSimpleName());
            pd.setProperty("message", ex.getMessage());
        }
        if (code.is5xxServerError()) log.error("5xx ResponseStatusException", ex);
        else log.warn("4xx ResponseStatusException: {}", ex.getReason());
        return ResponseEntity.status(code).body(pd);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex,
                                                      HttpServletRequest req) {
        String cause = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        log.warn("Malformed JSON on {} {}: {}", req.getMethod(), req.getRequestURI(), cause, ex);
        ApiError body = buildApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request", req.getRequestURI(), ex);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                       HttpServletRequest req) {
        String msg = String.format("Parameter '%s' must be of type %s",
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "required type");
        log.warn("Type mismatch on {} {}: {}", req.getMethod(), req.getRequestURI(), msg, ex);
        return ResponseEntity.badRequest().body(buildApiError(HttpStatus.BAD_REQUEST, msg, req.getRequestURI(), ex));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex,
                                                        HttpServletRequest req) {
        log.warn("DataIntegrityViolation on {} {}: {}", req.getMethod(), req.getRequestURI(),
                ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildApiError(HttpStatus.CONFLICT, "Data integrity violation", req.getRequestURI(), ex));
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ApiError> handleEmptyResult(EmptyResultDataAccessException ex,
                                                      HttpServletRequest req) {
        log.warn("EmptyResultDataAccess on {} {}", req.getMethod(), req.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildApiError(HttpStatus.NOT_FOUND, "Resource not found", req.getRequestURI(), ex));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest req) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Validation failed on {} {}: {}", req.getMethod(), req.getRequestURI(), details, ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(buildApiError(HttpStatus.BAD_REQUEST, details, req.getRequestURI(), ex));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex,
                                                   HttpServletRequest req) {
        log.warn("EntityNotFound on {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(buildApiError(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), ex));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiError> handleConflict(IllegalStateException ex,
                                                   HttpServletRequest req) {
        log.warn("IllegalState on {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(buildApiError(HttpStatus.CONFLICT, ex.getMessage(), req.getRequestURI(), ex));
    }

    @ExceptionHandler(TrackNotFoundException.class)
    public ResponseEntity<ApiError> handleTrackNotFound(TrackNotFoundException ex,
                                                        HttpServletRequest req) {
        log.warn("TrackNotFound on {} {}: {}", req.getMethod(), req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildApiError(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), ex));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnknown(Exception ex, HttpServletRequest req) {
        // LOG ROOT-CAUSE kèm traceId
        log.error("Unhandled error on {} {} traceId={}", req.getMethod(), req.getRequestURI(), traceId(), ex);
        // Ẩn message ở prod, hiện ở dev
        String msg = isDev() ? (ex.getClass().getSimpleName() + ": " + ex.getMessage()) : "Internal Server Error";
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildApiError(HttpStatus.INTERNAL_SERVER_ERROR, msg, req.getRequestURI(), ex));
    }
}
