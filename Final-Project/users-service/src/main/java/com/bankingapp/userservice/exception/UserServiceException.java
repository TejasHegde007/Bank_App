package com.bankingapp.userservice.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class UserServiceException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> details;

    public UserServiceException(String errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }
}
