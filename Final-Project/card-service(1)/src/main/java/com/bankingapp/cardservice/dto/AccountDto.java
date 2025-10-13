package com.bankingapp.cardservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Lightweight representation of an account returned from account-service.
 * Must match the JSON structure exposed by account-service.
 */
@Getter
@Setter
public class AccountDto {

    private String id;          // account ID
    private String userId;      // owner reference (used for validation)
    private String accountType; // e.g. SAVINGS, CURRENT
    private BigDecimal balance; // current balance
}
