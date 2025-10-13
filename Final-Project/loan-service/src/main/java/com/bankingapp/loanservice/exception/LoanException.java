package com.bankingapp.loanservice.exception;

import lombok.Getter;

@Getter
public class LoanException extends RuntimeException {
    private final ErrorCode errorCode;

    public LoanException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public LoanException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
