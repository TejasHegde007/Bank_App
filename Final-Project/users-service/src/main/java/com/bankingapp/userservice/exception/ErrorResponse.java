package com.bankingapp.userservice.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String message;
    private String traceId;
    private Map<String, Object> details;
    private LocalDateTime timestamp;
}
