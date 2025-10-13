package com.bankingapp.cardservice.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            ErrorCode errorCode, String message, String path) {

        ErrorResponse response = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus().value())
                .error(errorCode.name())
                .message(message != null ? message : errorCode.getDefaultMessage())
                .path(path)
                .build();

        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    @ExceptionHandler(CardException.class)
    public ResponseEntity<ErrorResponse> handleApiException(CardException ex, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.API_EXCEPTION, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse(ErrorCode.VALIDATION_ERROR.getDefaultMessage());
        return buildErrorResponse(ErrorCode.VALIDATION_ERROR, message, request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.CONSTRAINT_VIOLATION, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> handleFeignNotFound(FeignException.NotFound ex, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.FEIGN_NOT_FOUND, null, request.getRequestURI());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignGeneral(FeignException ex, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.FEIGN_ERROR, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        return buildErrorResponse(ErrorCode.INTERNAL_ERROR, ex.getMessage(), request.getRequestURI());
    }
}
