package com.bankingapp.loanservice.dto;

import com.bankingapp.loanservice.enums.LoanStatus;
import com.bankingapp.loanservice.enums.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class LoanResponse {
    private Long id;
    private String loanNumber;
    private String userId;
    private String accountId;
    private LoanType loanType;
    private LoanStatus loanStatus;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private BigDecimal emiAmount;
    private BigDecimal totalPayableAmount;
    private LocalDate disbursementDate;
    private LocalDate endDate;
}
