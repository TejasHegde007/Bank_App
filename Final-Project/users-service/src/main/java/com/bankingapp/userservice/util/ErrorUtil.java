package com.bankingapp.userservice.util;

import com.bankingapp.userservice.exception.ErrorResponse;
import com.bankingapp.userservice.exception.UserServiceException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ErrorUtil {

    public ErrorResponse buildError(UserServiceException ex) {
        return ErrorResponse.builder()
                .errorCode(ex.getErrorCode())
                .message(ex.getMessage())
                .traceId(UUID.randomUUID().toString())
                .details(ex.getDetails())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
