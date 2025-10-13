package com.bankingapp.loanservice.controller;

import com.bankingapp.loanservice.dto.LoanCreateRequest;
import com.bankingapp.loanservice.dto.LoanResponse;
import com.bankingapp.loanservice.entity.Loan;
import com.bankingapp.loanservice.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    // ✅ Create Loan (auto-approved)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanCreateRequest request) {
        Loan loan = mapToEntity(request);
        Loan savedLoan = loanService.createLoan(loan);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(savedLoan));
    }

    // ✅ Get Loan by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<LoanResponse> getLoanById(@PathVariable Long id) {
        Loan loan = loanService.getLoanById(id);
        return ResponseEntity.ok(mapToResponse(loan));
    }

    // ✅ Get Loans by User ID
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<LoanResponse>> getLoansByUserId(@PathVariable String userId) {
        List<LoanResponse> loans = loanService.getLoansByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    // ✅ Get Loans by Account ID
    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<LoanResponse>> getLoansByAccountId(@PathVariable String accountId) {
        List<LoanResponse> loans = loanService.getLoansByAccountId(accountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(loans);
    }

    // ✅ Close Loan
    @PutMapping("/{id}/close")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<LoanResponse> closeLoan(@PathVariable Long id) {
        Loan loan = loanService.closeLoan(id);
        return ResponseEntity.ok(mapToResponse(loan));
    }

    // ✅ Delete Loan
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLoan(@PathVariable Long id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }

    // 🔧 DTO Mapping Helpers
    private Loan mapToEntity(LoanCreateRequest request) {
        return Loan.builder()
                .userId(request.getUserId())
                .accountId(request.getAccountId())
                .loanType(request.getLoanType())
                .principalAmount(request.getPrincipalAmount())
                .interestRate(request.getInterestRate())
                .tenureMonths(request.getTenureMonths())
                .build();
    }

    private LoanResponse mapToResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .loanNumber(loan.getLoanNumber())
                .userId(loan.getUserId())
                .accountId(loan.getAccountId())
                .loanType(loan.getLoanType())
                .loanStatus(loan.getLoanStatus())
                .principalAmount(loan.getPrincipalAmount())
                .interestRate(loan.getInterestRate())
                .tenureMonths(loan.getTenureMonths())
                .emiAmount(loan.getEmiAmount())
                .totalPayableAmount(loan.getTotalPayableAmount())
                .disbursementDate(loan.getDisbursementDate())
                .endDate(loan.getEndDate())
                .build();
    }
}
