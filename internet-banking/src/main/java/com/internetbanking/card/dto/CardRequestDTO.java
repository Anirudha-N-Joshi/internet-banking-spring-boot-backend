package com.internetbanking.card.dto;

import com.internetbanking.card.entity.CardType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CardRequestDTO {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Card type is required")
    private CardType cardType;

    // only required if cardType is CREDIT
    private BigDecimal creditLimit;

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }

    public CardType getCardType() { return cardType; }
    public void setCardType(CardType cardType) { this.cardType = cardType; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }


}