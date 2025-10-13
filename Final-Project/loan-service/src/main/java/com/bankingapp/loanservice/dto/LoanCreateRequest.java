package com.bankingapp.loanservice.dto;

import com.bankingapp.loanservice.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanCreateRequest {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Account ID is required")
    private String accountId;

    @NotNull(message = "Loan type is required")
    private LoanType loanType;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "1000.0", message = "Minimum loan amount is 1000")
    private BigDecimal principalAmount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "1.0", message = "Minimum interest rate is 1%")
    private BigDecimal interestRate;

    @NotNull(message = "Tenure (in months) is required")
    private Integer tenureMonths;
}
