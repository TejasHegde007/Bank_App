package com.bankingapp.accountservice.dto;

import lombok.Data;

@Data
public class UserSummaryDto {
    private Long userId;
    private String username;
    private String email;
}
