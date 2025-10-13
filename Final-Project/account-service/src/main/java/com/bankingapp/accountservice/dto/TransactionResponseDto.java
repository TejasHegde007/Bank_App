package com.bankingapp.accountservice.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {
    private Long transactionId;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transactionDate;
}
