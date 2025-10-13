package com.bankingapp.accountservice.controller;

import com.bankingapp.accountservice.dto.TransactionRequestDto;
import com.bankingapp.accountservice.dto.TransactionResponseDto;
import com.bankingapp.accountservice.dto.TransferRequestDto;
import com.bankingapp.accountservice.dto.TransferResponseDto;
import com.bankingapp.accountservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@Validated
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Create transaction (CREDIT/DEBIT)
     * POST /transactions
     */
    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody TransactionRequestDto dto) {
        TransactionResponseDto created = transactionService.createTransaction(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getTransactionId())
                .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Get transactions by account
     * GET /transactions/account/{accountId}
     */
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDto>> getByAccount(@PathVariable("accountId") Long accountId) {
        List<TransactionResponseDto> list = transactionService.getTransactionsByAccount(accountId);
        return ResponseEntity.ok(list);
    }

    /**
     * Get transaction by id
     * GET /transactions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(@PathVariable("id") Long id) {
        TransactionResponseDto dto = transactionService.getTransactionById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDto> transfer(@Valid @RequestBody TransferRequestDto dto) {
        TransferResponseDto resp = transactionService.transferBetweenAccounts(dto);
        return ResponseEntity.ok(resp);
    }


}
