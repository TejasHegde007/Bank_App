package com.bankingapp.cardservice.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Standard error response structure used across all microservices.
 */
@Getter
@Builder
public class ErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error; // Enum name like "VALIDATION_ERROR"
    private final String message;
    private final String path;
}
