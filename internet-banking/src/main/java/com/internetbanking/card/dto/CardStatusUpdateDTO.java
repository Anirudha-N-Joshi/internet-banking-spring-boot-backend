package com.internetbanking.card.dto;

import com.internetbanking.card.entity.CardStatus;
import jakarta.validation.constraints.NotNull;

public class CardStatusUpdateDTO {

    @NotNull(message = "Status is required")
    private CardStatus status;

    public CardStatus getStatus() { return status; }
    public void setStatus(CardStatus status) { this.status = status; }
}