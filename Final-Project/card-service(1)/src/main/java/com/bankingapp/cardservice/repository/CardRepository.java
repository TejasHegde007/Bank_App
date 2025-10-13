package com.bankingapp.cardservice.repository;

import com.bankingapp.cardservice.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByUserId(String userId);

    List<Card> findByAccountId(String accountId);

    Optional<Card> findByCardNumber(String cardNumber);
}
