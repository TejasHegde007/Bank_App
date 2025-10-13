package com.bankingapp.loanservice.service.impl;

import com.bankingapp.loanservice.client.AccountClient;
import com.bankingapp.loanservice.client.UserClient;
import com.bankingapp.loanservice.dto.AccountDto;
import com.bankingapp.loanservice.dto.UserDto;
import com.bankingapp.loanservice.entity.Loan;
import com.bankingapp.loanservice.enums.LoanStatus;
import com.bankingapp.loanservice.exception.ErrorCode;
import com.bankingapp.loanservice.exception.LoanException;
import com.bankingapp.loanservice.repository.LoanRepository;
import com.bankingapp.loanservice.service.LoanService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final UserClient userClient;
    private final AccountClient accountClient;

    @Override
    public Loan createLoan(Loan loan) {
        // ✅ Validate User
        UserDto user;
        try {
            user = userClient.getUserById(loan.getUserId());
        } catch (FeignException.NotFound ex) {
            throw new LoanException(ErrorCode.USER_NOT_FOUND);
        } catch (Exception ex) {
            throw new LoanException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to validate user: " + ex.getMessage());
        }

        // ✅ Validate Account
        AccountDto account;
        try {
            account = accountClient.getAccountById(loan.getAccountId());
        } catch (FeignException.NotFound ex) {
            throw new LoanException(ErrorCode.ACCOUNT_NOT_FOUND);
        } catch (Exception ex) {
            throw new LoanException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to validate account: " + ex.getMessage());
        }

        // ✅ Ensure account belongs to user
        if (!account.getUserId().equals(loan.getUserId())) {
            throw new LoanException(ErrorCode.ACCOUNT_USER_MISMATCH);
        }

        // ✅ Auto-approve loan
        loan.setLoanStatus(LoanStatus.APPROVED);

        // ✅ Generate loan number if not present
        if (loan.getLoanNumber() == null || loan.getLoanNumber().isBlank()) {
            loan.setLoanNumber(generateLoanNumber());
        }

        return loanRepository.save(loan);
    }

    @Override
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanException(ErrorCode.LOAN_NOT_FOUND, "Loan not found with ID: " + id));
    }

    @Override
    public List<Loan> getLoansByUserId(String userId) {
        List<Loan> loans = loanRepository.findByUserId(userId);
        if (loans.isEmpty()) {
            throw new LoanException(ErrorCode.LOAN_NOT_FOUND, "No loans found for user ID: " + userId);
        }
        return loans;
    }

    @Override
    public List<Loan> getLoansByAccountId(String accountId) {
        List<Loan> loans = loanRepository.findByAccountId(accountId);
        if (loans.isEmpty()) {
            throw new LoanException(ErrorCode.LOAN_NOT_FOUND, "No loans found for account ID: " + accountId);
        }
        return loans;
    }

    @Override
    public Loan closeLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanException(ErrorCode.LOAN_NOT_FOUND, "Loan not found with ID: " + id));

        if (loan.getLoanStatus() == LoanStatus.CLOSED) {
            throw new LoanException(ErrorCode.LOAN_ALREADY_CLOSED);
        }

        loan.setLoanStatus(LoanStatus.CLOSED);
        return loanRepository.save(loan);
    }

    @Override
    public void deleteLoan(Long id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanException(ErrorCode.LOAN_NOT_FOUND, "Loan not found with ID: " + id));
        loanRepository.delete(loan);
    }

    // 🔧 Utility method
    private String generateLoanNumber() {
        return "LN" + System.currentTimeMillis();
    }
}
