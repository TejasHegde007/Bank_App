package com.bankingapp.loanservice.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Invalid input provided"),

    // Loan specific
    LOAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Loan not found"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Account not found"),
    ACCOUNT_USER_MISMATCH(HttpStatus.BAD_REQUEST, "Account does not belong to the user"),
    LOAN_ALREADY_CLOSED(HttpStatus.BAD_REQUEST, "Loan is already closed");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
