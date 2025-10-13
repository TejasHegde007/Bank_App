package com.bankingapp.accountservice.service;

import com.bankingapp.accountservice.client.UserServiceClient;
import com.bankingapp.accountservice.dto.AccountRequestDto;
import com.bankingapp.accountservice.dto.AccountResponseDto;
import com.bankingapp.accountservice.entity.Account;
import com.bankingapp.accountservice.exception.ErrorCode;
import com.bankingapp.accountservice.exception.ServiceException;
import com.bankingapp.accountservice.repository.AccountRepository;
import com.bankingapp.accountservice.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto dto) {
        ServiceUtils.validateAccountRequest(dto);

        // ✅ Step 1: Verify user exists in UserService
        try {
            userServiceClient.getUserById(dto.getUserId());
        } catch (feign.FeignException.NotFound e) {
            throw new ServiceException(ErrorCode.INVALID_ACCOUNT_REQUEST,
                    "User not found with id: " + dto.getUserId(),
                    Map.of("userId", dto.getUserId()));
        } catch (Exception e) {
            throw new ServiceException(ErrorCode.SERVICE_UNAVAILABLE,
                    "Failed to connect to UserService",
                    Map.of("error", e.getMessage()));
        }

        // ✅ Step 2: Generate account number and save
        String accountNumber = ServiceUtils.generateAccountNumber();

        Account account = Account.builder()
                .userId(dto.getUserId())
                .accountNumber(accountNumber)
                .accountType(dto.getAccountType())
                .balance(dto.getInitialBalance() != null ? dto.getInitialBalance() : BigDecimal.ZERO)
                .build();

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    @Override
    public List<AccountResponseDto> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponseDto getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ServiceException(
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        "Account not found with ID: " + id,
                        Map.of("accountId", id)
                ));
        return mapToResponse(account);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ServiceException(
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        "Account not found with ID: " + id,
                        Map.of("accountId", id)
                ));
        accountRepository.delete(account);
        log.info("Deleted account id={}", id);
    }

    private AccountResponseDto mapToResponse(Account account) {
        return AccountResponseDto.builder()
                .accountId(account.getAccountId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType() != null ? account.getAccountType().toString() : null)
                .balance(account.getBalance())
                .userId(account.getUserId())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
