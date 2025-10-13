package com.bankingapp.accountservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // ---- ACCOUNT ERRORS ----
    ACCOUNT_NOT_FOUND("ERR_101", "Account not found", HttpStatus.NOT_FOUND),
    INVALID_ACCOUNT_REQUEST("ERR_102", "Invalid account request", HttpStatus.BAD_REQUEST),

    // ---- TRANSACTION ERRORS ----
    TRANSACTION_NOT_FOUND("ERR_103", "Transaction not found", HttpStatus.NOT_FOUND),
    INVALID_TRANSACTION("ERR_104", "Invalid transaction request", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_FUNDS("ERR_105", "Insufficient balance for debit transaction", HttpStatus.CONFLICT),

    // ---- GENERAL ----
    INTERNAL_SERVER_ERROR("ERR_999", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    VALIDATION_ERROR("ERR_100", "Validation failed", HttpStatus.BAD_REQUEST),
    TRANSFER_SAME_ACCOUNT("ERR_106", "From and To account cannot be same", HttpStatus.BAD_REQUEST),
    TRANSFER_FAILED("ERR_107", "Transfer failed due to concurrent update — please retry", HttpStatus.CONFLICT),
    SERVICE_UNAVAILABLE("ERR_105", "Dependent service unavailable", HttpStatus.SERVICE_UNAVAILABLE),
    ;

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
