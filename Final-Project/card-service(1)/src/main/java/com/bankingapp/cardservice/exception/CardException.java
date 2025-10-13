package com.bankingapp.cardservice.exception;

/**
 * Custom exception for all card-related business logic errors.
 * Mirrors ApiException from other services but domain-specific.
 */
public class CardException extends RuntimeException {

    public CardException(String message) {
        super(message);
    }

    public CardException(String message, Throwable cause) {
        super(message, cause);
    }
}
