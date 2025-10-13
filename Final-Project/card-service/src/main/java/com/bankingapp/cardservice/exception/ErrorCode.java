package com.bankingapp.cardservice.exception;

import org.springframework.http.HttpStatus;

/**
 * Standard error codes used across all microservices.
 * Keeps responses consistent and eliminates magic strings.
 */
public enum ErrorCode {

    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation failed"),
    CONSTRAINT_VIOLATION(HttpStatus.BAD_REQUEST, "Constraint violation"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    FEIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "Referenced resource not found in another service"),
    FEIGN_ERROR(HttpStatus.BAD_GATEWAY, "Error calling external service"),
    API_EXCEPTION(HttpStatus.BAD_REQUEST, "Application-level exception"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
