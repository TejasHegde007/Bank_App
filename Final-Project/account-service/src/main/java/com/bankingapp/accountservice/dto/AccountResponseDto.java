package com.bankingapp.accountservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDto {
    private Long accountId;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
