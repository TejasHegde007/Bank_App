package com.bankingapp.loanservice.service;

import com.bankingapp.loanservice.entity.Loan;

import java.util.List;

public interface LoanService {

    /**
     * Creates a new loan (auto-approved upon creation).
     *
     * @param loan Loan entity containing basic loan info
     * @return Created loan
     */
    Loan createLoan(Loan loan);

    /**
     * Retrieves loan details by ID.
     *
     * @param id Loan ID
     * @return Loan entity
     */
    Loan getLoanById(Long id);

    /**
     * Retrieves all loans for a specific user.
     *
     * @param userId User ID
     * @return List of loans for that user
     */
    List<Loan> getLoansByUserId(String userId);

    /**
     * Retrieves all loans for a specific account.
     *
     * @param accountId Account ID
     * @return List of loans for that account
     */
    List<Loan> getLoansByAccountId(String accountId);

    /**
     * Closes a loan by its ID (marks status as CLOSED).
     *
     * @param id Loan ID
     * @return Updated loan entity
     */
    Loan closeLoan(Long id);

    /**
     * Deletes a loan permanently by its ID.
     *
     * @param id Loan ID
     */
    void deleteLoan(Long id);
}
