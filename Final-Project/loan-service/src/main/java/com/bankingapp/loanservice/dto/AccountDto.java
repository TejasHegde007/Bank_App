package com.bankingapp.loanservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AccountDto {
    private String id;
    private String userId;
    private BigDecimal balance;
}
