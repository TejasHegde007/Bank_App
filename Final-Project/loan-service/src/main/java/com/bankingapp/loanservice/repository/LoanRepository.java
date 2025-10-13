package com.bankingapp.loanservice.repository;

import com.bankingapp.loanservice.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Find all loans for a particular user
    List<Loan> findByUserId(String userId);

    // Find all loans for a specific account
    List<Loan> findByAccountId(String accountId);

    // Optional: find by loan number
    Loan findByLoanNumber(String loanNumber);
}
