package com.bankingapp.accountservice.service;

import com.bankingapp.accountservice.dto.TransactionRequestDto;
import com.bankingapp.accountservice.dto.TransactionResponseDto;
import com.bankingapp.accountservice.dto.TransferRequestDto;
import com.bankingapp.accountservice.dto.TransferResponseDto;
import com.bankingapp.accountservice.entity.Account;
import com.bankingapp.accountservice.entity.Transaction;
import com.bankingapp.accountservice.enums.TransactionType;
import com.bankingapp.accountservice.exception.ErrorCode;
import com.bankingapp.accountservice.exception.ServiceException;
import com.bankingapp.accountservice.repository.AccountRepository;
import com.bankingapp.accountservice.repository.TransactionRepository;
import com.bankingapp.accountservice.util.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PlatformTransactionManager transactionManager;

    private static final int MAX_TRANSFER_ATTEMPTS = 3;

    @Override
    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto dto) {
        // validate request
        ServiceUtils.validateTransactionRequest(dto);

        // load account (managed entity)
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new ServiceException(
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        "Account not found with ID: " + dto.getAccountId(),
                        Map.of("accountId", dto.getAccountId())
                ));

        // calculate new balance
        BigDecimal newBalance = calculateNewBalance(account, dto);

        // apply balance and persist transaction
        account.setBalance(newBalance);

        Transaction tx = Transaction.builder()
                .account(account)
                .amount(dto.getAmount())
                .transactionType(dto.getTransactionType())
                .description(dto.getDescription())
                .build();

        // Persist transaction (account is managed, so balance update will be flushed)
        transactionRepository.save(tx);
        accountRepository.save(account); // explicit save to be clear and compatible across setups

        log.info("Transaction created id={} type={} amount={} accountId={} newBalance={}",
                tx.getTransactionId(), tx.getTransactionType(), tx.getAmount(), account.getAccountId(), newBalance);

        return mapToResponse(tx);
    }

    @Override
    public List<TransactionResponseDto> getTransactionsByAccount(Long accountId) {
        // validate account existence early
        if (!accountRepository.existsById(accountId)) {
            throw new ServiceException(ErrorCode.ACCOUNT_NOT_FOUND,
                    "Account not found with ID: " + accountId,
                    Map.of("accountId", accountId));
        }

        return transactionRepository.findByAccount_AccountId(accountId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponseDto getTransactionById(Long transactionId) {
        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ServiceException(
                        ErrorCode.TRANSACTION_NOT_FOUND,
                        "Transaction not found with ID: " + transactionId,
                        Map.of("transactionId", transactionId)
                ));

        return mapToResponse(tx);
    }

    @Override
    @Transactional
    public TransferResponseDto transferBetweenAccounts(TransferRequestDto dto) {
        Account from = accountRepository.findById(dto.getFromAccountId())
                .orElseThrow(() -> new ServiceException(
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        "Source account not found",
                        Map.of("accountId", dto.getFromAccountId())
                ));

        Account to = accountRepository.findById(dto.getToAccountId())
                .orElseThrow(() -> new ServiceException(
                        ErrorCode.ACCOUNT_NOT_FOUND,
                        "Destination account not found",
                        Map.of("accountId", dto.getToAccountId())
                ));

        if (from.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new ServiceException(ErrorCode.INSUFFICIENT_FUNDS,
                    "Insufficient balance for transfer",
                    Map.of("fromAccountId", dto.getFromAccountId()));
        }

        // Perform balance updates
        from.setBalance(from.getBalance().subtract(dto.getAmount()));
        to.setBalance(to.getBalance().add(dto.getAmount()));

        // Create corresponding transactions
        Transaction debitTx = Transaction.builder()
                .account(from)
                .amount(dto.getAmount())
                .transactionType(TransactionType.DEBIT)
                .description("Transfer to account " + to.getAccountNumber())
                .build();

        Transaction creditTx = Transaction.builder()
                .account(to)
                .amount(dto.getAmount())
                .transactionType(TransactionType.CREDIT)
                .description("Transfer from account " + from.getAccountNumber())
                .build();

        // Save both transactions (atomic under @Transactional)
        transactionRepository.save(debitTx);
        transactionRepository.save(creditTx);

        // explicit save accounts
        accountRepository.save(from);
        accountRepository.save(to);

        return TransferResponseDto.builder()
                .debitTransactionId(debitTx.getTransactionId())
                .creditTransactionId(creditTx.getTransactionId())
                .fromAccountId(from.getAccountId())
                .toAccountId(to.getAccountId())
                .amount(dto.getAmount())
                .timestamp(LocalDateTime.now())
                .message("Transfer successful")
                .build();
    }


    // ----- helpers ----- //

    private BigDecimal calculateNewBalance(Account account, TransactionRequestDto dto) {
        BigDecimal currentBalance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        TransactionType type = dto.getTransactionType();

        if (type == TransactionType.CREDIT) {
            return currentBalance.add(dto.getAmount());
        }

        if (type == TransactionType.DEBIT) {
            if (currentBalance.compareTo(dto.getAmount()) < 0) {
                throw new ServiceException(ErrorCode.INSUFFICIENT_FUNDS,
                        "Insufficient balance for debit",
                        Map.of("accountId", account.getAccountId(), "currentBalance", currentBalance.toString(), "attempted", dto.getAmount().toString()));
            }
            return currentBalance.subtract(dto.getAmount());
        }

        throw new ServiceException(ErrorCode.INVALID_TRANSACTION, "Unsupported transaction type",
                Map.of("transactionType", dto.getTransactionType()));
    }

    private TransactionResponseDto mapToResponse(Transaction tx) {
        return TransactionResponseDto.builder()
                .transactionId(tx.getTransactionId())
                .transactionType(tx.getTransactionType() != null ? tx.getTransactionType().toString() : null)
                .amount(tx.getAmount())
                .description(tx.getDescription())
                .transactionDate(tx.getTransactionDate())
                .build();
    }
}
