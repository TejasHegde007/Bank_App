package com.bankingapp.cardservice.dto;

import com.bankingapp.cardservice.enums.CardType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO returned to clients after card operations.
 * Hides internal fields and ORM metadata.
 */
@Getter
@Builder
public class CardResponse {

    private Long id;
    private String cardNumber;
    private CardType cardType;
    private String cardHolderName;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private BigDecimal creditLimit;
    private BigDecimal availableLimit;
    private Boolean isBlocked;
    private String userId;
    private String accountId;
}
