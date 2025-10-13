package com.bankingapp.cardservice.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Lightweight representation of a user returned from user-service.
 * Must match user-service JSON field names.
 */
@Getter
@Setter
public class UserDto {
    private String id;
    private String fullName;
    private String email;
}
