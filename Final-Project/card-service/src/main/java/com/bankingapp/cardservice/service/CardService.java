package com.bankingapp.cardservice.service;

import com.bankingapp.cardservice.entity.Card;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing cards (Debit/Credit).
 * Defines all business operations for card creation, updates, and retrieval.
 */
public interface CardService {

    /**
     * Create a new card for a user and account.
     *
     * @param card Card entity containing userId, accountId, and cardType.
     * @return Saved Card entity.
     */
    Card createCard(Card card);

    /**
     * Retrieve a card by its ID.
     *
     * @param id Card ID.
     * @return Card entity.
     */
    Card getCardById(Long id);

    /**
     * Get all cards belonging to a specific user.
     *
     * @param userId User ID.
     * @return List of cards.
     */
    List<Card> getCardsByUserId(String userId);

    /**
     * Block or unblock a specific card.
     *
     * @param id Card ID.
     * @param block true to block, false to unblock.
     * @return Updated card entity.
     */
    Card blockCard(Long id, boolean block);

    /**
     * Update credit limit for a credit card.
     *
     * @param id Card ID.
     * @param newLimit New credit limit amount.
     * @return Updated card entity.
     */
    Card updateLimit(Long id, BigDecimal newLimit);

    /**
     * Delete a card by ID.
     *
     * @param id Card ID.
     */
    void deleteCard(Long id);
}
