package com.bankingapp.accountservice.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ServiceException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Map<String, Object> details;

    public ServiceException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public ServiceException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
        this.details = null;
    }

    public ServiceException(ErrorCode errorCode, String customMessage, Map<String, Object> details) {
        super(customMessage);
        this.errorCode = errorCode;
        this.details = details;
    }
}
