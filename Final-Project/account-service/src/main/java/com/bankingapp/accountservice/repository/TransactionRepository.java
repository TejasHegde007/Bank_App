package com.bankingapp.accountservice.repository;

import com.bankingapp.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccount_AccountId(Long accountId);
}
