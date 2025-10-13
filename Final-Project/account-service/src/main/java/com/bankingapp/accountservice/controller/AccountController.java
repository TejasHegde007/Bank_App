package com.bankingapp.accountservice.controller;

import com.bankingapp.accountservice.dto.AccountRequestDto;
import com.bankingapp.accountservice.dto.AccountResponseDto;
import com.bankingapp.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/accounts")
@Validated
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /**
     * Create account
     * POST /accounts
     */
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountRequestDto dto) {
        AccountResponseDto created = accountService.createAccount(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getAccountId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Get list of accounts
     * GET /accounts
     */
    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
        List<AccountResponseDto> list = accountService.getAllAccounts();
        return ResponseEntity.ok(list);
    }

    /**
     * Get account by id
     * GET /accounts/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(@PathVariable("id") Long id) {
        AccountResponseDto dto = accountService.getAccountById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * Delete account
     * DELETE /accounts/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}
