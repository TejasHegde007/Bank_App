package com.bankingapp.cardservice.enums;

/**
 * Represents the type of a card in the system.
 * - DEBIT: linked directly to user's account balance
 * - CREDIT: has a separate credit limit and repayment cycle
 */
public enum CardType {
    DEBIT,
    CREDIT
}
