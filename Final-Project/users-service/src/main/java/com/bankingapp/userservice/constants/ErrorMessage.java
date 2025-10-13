package com.bankingapp.userservice.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorMessage {
    USER_NOT_FOUND("ERR_01", "User does not exist", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS("ERR_02", "User already exists", HttpStatus.CONFLICT),
    INVALID_INPUT("ERR_03", "Invalid input data", HttpStatus.BAD_REQUEST),
    INTERNAL_ERROR("ERR_99", "Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
