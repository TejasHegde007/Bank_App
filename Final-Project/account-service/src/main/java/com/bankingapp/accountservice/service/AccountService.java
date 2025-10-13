package com.bankingapp.accountservice.service;

import com.bankingapp.accountservice.dto.AccountRequestDto;
import com.bankingapp.accountservice.dto.AccountResponseDto;

import java.util.List;

public interface AccountService {
    AccountResponseDto createAccount(AccountRequestDto dto);
    List<AccountResponseDto> getAllAccounts();
    AccountResponseDto getAccountById(Long id);
    void deleteAccount(Long id);
}
