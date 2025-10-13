package com.bankingapp.accountservice.exception;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException ex) {
        String traceId = getOrGenerateTraceId();

        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(errorCode.getCode())
                .message(ex.getMessage())
                .traceId(traceId)
                .details(ex.getDetails())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        String traceId = getOrGenerateTraceId();

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = ErrorResponse.builder()
                .errorCode(errorCode.getCode())
                .message("Unexpected error: " + ex.getMessage())
                .traceId(traceId)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    private String getOrGenerateTraceId() {
        String traceId = MDC.get("traceId");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            MDC.put("traceId", traceId);
        }
        return traceId;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String traceId = getOrGenerateTraceId();

        // collect field errors
        List<Map<String, String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "rejectedValue", String.valueOf(fe.getRejectedValue()),
                        "message", fe.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ErrorResponse resp = ErrorResponse.builder()
                .errorCode(ErrorCode.VALIDATION_ERROR.getCode())
                .message("Validation failed")
                .traceId(traceId)
                .details(Map.of("fieldErrors", fieldErrors))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus()).body(resp);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String traceId = getOrGenerateTraceId();

        List<Map<String, String>> violations = ex.getConstraintViolations()
                .stream()
                .map(cv -> Map.of(
                        "path", cv.getPropertyPath().toString(),
                        "invalidValue", String.valueOf(cv.getInvalidValue()),
                        "message", cv.getMessage()
                ))
                .collect(Collectors.toList());

        ErrorResponse resp = ErrorResponse.builder()
                .errorCode(ErrorCode.VALIDATION_ERROR.getCode())
                .message("Validation failed")
                .traceId(traceId)
                .details(Map.of("violations", violations))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getHttpStatus()).body(resp);
    }
}
