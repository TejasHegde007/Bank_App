package com.bankingapp.cardservice.dto;

import com.bankingapp.cardservice.enums.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating a new card request.
 * Keeps external API separate from internal entity.
 */
@Getter
@Setter
public class CardCreateRequest {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Account ID is required")
    private String accountId;

    @NotBlank(message = "Card holder name is required")
    private String cardHolderName;

    @NotNull(message = "Card type is required")
    private CardType cardType;
}
