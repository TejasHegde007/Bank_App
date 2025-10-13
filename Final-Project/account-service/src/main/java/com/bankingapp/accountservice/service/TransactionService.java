package com.bankingapp.accountservice.service;

import com.bankingapp.accountservice.dto.*;
import java.util.List;

public interface TransactionService {
    TransactionResponseDto createTransaction(TransactionRequestDto dto);
    List<TransactionResponseDto> getTransactionsByAccount(Long accountId);
    TransactionResponseDto getTransactionById(Long transactionId);

    // new:
    TransferResponseDto transferBetweenAccounts(TransferRequestDto dto);
}
