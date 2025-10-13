package com.bankingapp.accountservice.util;

import com.bankingapp.accountservice.dto.AccountRequestDto;
import com.bankingapp.accountservice.dto.TransactionRequestDto;
import com.bankingapp.accountservice.dto.TransferRequestDto;
import com.bankingapp.accountservice.enums.TransactionType;
import com.bankingapp.accountservice.exception.ErrorCode;
import com.bankingapp.accountservice.exception.ServiceException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public final class ServiceUtils {

    private ServiceUtils() {}

    public static String generateAccountNumber() {
        return "ACCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public static void validateAccountRequest(AccountRequestDto dto) {
        if (dto == null) {
            throw new ServiceException(ErrorCode.INVALID_ACCOUNT_REQUEST, "Account request cannot be null");
        }
        if (dto.getUserId() == null) {
            throw new ServiceException(ErrorCode.INVALID_ACCOUNT_REQUEST, "User ID cannot be null",
                    Map.of("field", "userId"));
        }
        if (dto.getAccountType() == null) {
            throw new ServiceException(ErrorCode.INVALID_ACCOUNT_REQUEST, "Account type must be specified",
                    Map.of("field", "accountType"));
        }
        if (dto.getInitialBalance() != null && dto.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new ServiceException(ErrorCode.INVALID_ACCOUNT_REQUEST, "Initial balance cannot be negative",
                    Map.of("initialBalance", dto.getInitialBalance().toString()));
        }
    }

    public static void validateTransactionRequest(TransactionRequestDto dto) {
        if (dto == null) {
            throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Transaction request cannot be null");
        }
        if (dto.getAccountId() == null) {
            throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Account ID is required",
                    Map.of("field", "accountId"));
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Amount must be greater than zero",
                    Map.of("field", "amount"));
        }
        if (dto.getTransactionType() == null) {
            throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Transaction type is required",
                    Map.of("field", "transactionType"));
        }
        // optional: check enum values explicitly (not needed if DTO already uses enum)
        if (!(dto.getTransactionType() == TransactionType.CREDIT || dto.getTransactionType() == TransactionType.DEBIT)) {
            throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Unsupported transaction type",
                    Map.of("transactionType", dto.getTransactionType()));
        }
    }

    public static void validateTransferRequest(TransferRequestDto dto) {
        if (dto == null) {
            throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Transfer request cannot be null");
        }
        if (dto.getFromAccountId() == null || dto.getToAccountId() == null) {
            throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Both fromAccountId and toAccountId are required");
        }
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Amount must be > 0");
        }
    }

}
