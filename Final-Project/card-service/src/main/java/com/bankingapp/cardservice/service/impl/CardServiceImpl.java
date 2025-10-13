package com.bankingapp.cardservice.service.impl;

import com.bankingapp.cardservice.client.AccountClient;
import com.bankingapp.cardservice.client.UserClient;
import com.bankingapp.cardservice.dto.AccountDto;
import com.bankingapp.cardservice.dto.UserDto;
import com.bankingapp.cardservice.entity.Card;
import com.bankingapp.cardservice.enums.CardType;
import com.bankingapp.cardservice.exception.CardException;
import com.bankingapp.cardservice.repository.CardRepository;
import com.bankingapp.cardservice.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserClient userClient;
    private final AccountClient accountClient;

    @Override
    public Card createCard(Card card) {
        // 1️⃣ Validate user existence
        UserDto user;
        try {
            user = userClient.getUserById(card.getUserId());
        } catch (Exception ex) {
            throw new CardException("User not found for ID: " + card.getUserId());
        }

        // 2️⃣ Validate account existence
        AccountDto account;
        try {
            account = accountClient.getAccountById(card.getAccountId());
        } catch (Exception ex) {
            throw new CardException("Account not found for ID: " + card.getAccountId());
        }

        // 3️⃣ Validate account-user linkage
        if (!account.getUserId().equals(card.getUserId())) {
            throw new CardException("Account does not belong to the specified user");
        }

        // 4️⃣ Prevent duplicate card numbers for same account
        List<Card> existingCards = cardRepository.findByAccountId(card.getAccountId());
        if (!existingCards.isEmpty()) {
            // Optional: limit one debit/credit per account type
            for (Card existing : existingCards) {
                if (existing.getCardType() == card.getCardType()) {
                    throw new CardException("A " + card.getCardType() + " card already exists for this account");
                }
            }
        }

        // 5️⃣ Auto-generate card number
        card.setCardNumber(generateCardNumber());

        // 6️⃣ Default fields
        card.setIssueDate(LocalDate.now());
        card.setExpiryDate(LocalDate.now().plusYears(5));
        card.setIsBlocked(false);

        // 7️⃣ Handle credit card logic
        if (card.getCardType() == CardType.CREDIT) {
            if (card.getCreditLimit() == null) {
                card.setCreditLimit(BigDecimal.valueOf(100000)); // default 1L limit
            }
            card.setAvailableLimit(card.getCreditLimit());
        } else {
            card.setCreditLimit(null);
            card.setAvailableLimit(null);
        }

        // 8️⃣ Save card
        return cardRepository.save(card);
    }

    @Override
    public Card getCardById(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardException("Card not found with ID: " + id));
    }

    @Override
    public List<Card> getCardsByUserId(String userId) {
        return cardRepository.findByUserId(userId);
    }

    @Override
    public Card blockCard(Long id, boolean block) {
        Card card = getCardById(id);
        card.setIsBlocked(block);
        return cardRepository.save(card);
    }

    @Override
    public Card updateLimit(Long id, BigDecimal newLimit) {
        Card card = getCardById(id);
        if (card.getCardType() != CardType.CREDIT) {
            throw new CardException("Limit updates are only allowed for credit cards");
        }
        card.setCreditLimit(newLimit);
        card.setAvailableLimit(newLimit);
        return cardRepository.save(card);
    }

    @Override
    public void deleteCard(Long id) {
        Card card = getCardById(id);
        cardRepository.delete(card);
    }

    // 🔧 Helper: 12-digit pseudo card number generator
    private String generateCardNumber() {
        String prefix = "4000";
        StringBuilder number = new StringBuilder(prefix);
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            number.append(random.nextInt(10));
        }

        return number.toString();
    }
}
