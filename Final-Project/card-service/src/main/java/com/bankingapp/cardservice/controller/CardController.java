package com.bankingapp.cardservice.controller;

import com.bankingapp.cardservice.dto.CardCreateRequest;
import com.bankingapp.cardservice.dto.CardResponse;
import com.bankingapp.cardservice.entity.Card;
import com.bankingapp.cardservice.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for managing cards (Debit & Credit).
 * Uses DTOs to separate API layer from database entities.
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // ---------------- CREATE ----------------
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<CardResponse> createCard(@Valid @RequestBody CardCreateRequest request) {
        Card card = Card.builder()
                .userId(request.getUserId())
                .accountId(request.getAccountId())
                .cardHolderName(request.getCardHolderName())
                .cardType(request.getCardType())
                .build();

        Card created = cardService.createCard(card);
        return new ResponseEntity<>(toResponse(created), HttpStatus.CREATED);
    }

    // ---------------- READ ----------------
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long id) {
        Card card = cardService.getCardById(id);
        return ResponseEntity.ok(toResponse(card));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardResponse>> getCardsByUserId(@PathVariable String userId) {
        List<CardResponse> responses = cardService.getCardsByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ---------------- UPDATE ----------------
    @PutMapping("/{id}/block")
    public ResponseEntity<CardResponse> blockCard(@PathVariable Long id, @RequestParam boolean block) {
        Card card = cardService.blockCard(id, block);
        return ResponseEntity.ok(toResponse(card));
    }

    @PutMapping("/{id}/limit")
    public ResponseEntity<CardResponse> updateLimit(@PathVariable Long id, @RequestParam BigDecimal newLimit) {
        Card card = cardService.updateLimit(id, newLimit);
        return ResponseEntity.ok(toResponse(card));
    }

    // ---------------- DELETE ----------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- PRIVATE MAPPER ----------------
    private CardResponse toResponse(Card card) {
        return CardResponse.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber())
                .cardType(card.getCardType())
                .cardHolderName(card.getCardHolderName())
                .issueDate(card.getIssueDate())
                .expiryDate(card.getExpiryDate())
                .creditLimit(card.getCreditLimit())
                .availableLimit(card.getAvailableLimit())
                .isBlocked(card.getIsBlocked())
                .userId(card.getUserId())
                .accountId(card.getAccountId())
                .build();
    }
}
