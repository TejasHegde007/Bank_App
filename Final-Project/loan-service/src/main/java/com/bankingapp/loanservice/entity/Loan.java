package com.bankingapp.loanservice.entity;

import com.bankingapp.loanservice.enums.LoanStatus;
import com.bankingapp.loanservice.enums.LoanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "loan_seq_gen")
    @SequenceGenerator(name = "loan_seq_gen", sequenceName = "loan_seq", allocationSize = 1)
    private Long id;

    @NotNull(message = "User ID cannot be null")
    @Column(name = "user_id", nullable = false)
    private String userId;

    @NotNull(message = "Account ID cannot be null")
    @Column(name = "account_id", nullable = false)
    private String accountId;

    @NotBlank(message = "Loan number cannot be blank")
    @Column(name = "loan_number", unique = true, nullable = false, length = 15)
    private String loanNumber;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Loan type cannot be null")
    @Column(name = "loan_type", nullable = false)
    private LoanType loanType;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Loan status cannot be null")
    @Column(name = "loan_status", nullable = false)
    private LoanStatus loanStatus;

    @NotNull(message = "Principal amount is required")
    @DecimalMin(value = "1000.0", message = "Minimum loan amount should be 1000")
    @Column(name = "principal_amount", nullable = false)
    private BigDecimal principalAmount;

    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "1.0", message = "Minimum interest rate is 1%")
    @Column(name = "interest_rate", nullable = false)
    private BigDecimal interestRate;

    @NotNull(message = "Tenure is required")
    @Column(name = "tenure_months", nullable = false)
    private Integer tenureMonths;

    @Column(name = "emi_amount", nullable = false)
    private BigDecimal emiAmount;

    @Column(name = "total_payable_amount", nullable = false)
    private BigDecimal totalPayableAmount;

    @Column(name = "disbursement_date")
    private LocalDate disbursementDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @PrePersist
    public void prePersist() {
        // Auto-approved loan
        this.loanStatus = LoanStatus.APPROVED;
        this.disbursementDate = LocalDate.now();

        if (loanNumber == null || loanNumber.isBlank()) {
            this.loanNumber = generateLoanNumber();
        }

        calculateEmiAndTotal();
    }

    private String generateLoanNumber() {
        return "LN" + System.currentTimeMillis();
    }

    private void calculateEmiAndTotal() {
        // Simple Interest = (P × R × T) / 100
        BigDecimal interest = principalAmount
                .multiply(interestRate)
                .multiply(BigDecimal.valueOf(tenureMonths / 12.0))
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

        totalPayableAmount = principalAmount.add(interest);
        emiAmount = totalPayableAmount.divide(BigDecimal.valueOf(tenureMonths), 2, BigDecimal.ROUND_HALF_UP);
        endDate = disbursementDate.plusMonths(tenureMonths);
    }
}
