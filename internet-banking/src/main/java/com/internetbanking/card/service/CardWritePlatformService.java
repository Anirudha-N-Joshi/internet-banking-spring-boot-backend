package com.internetbanking.card.service;

import com.internetbanking.card.dto.CardRequestDTO;
import com.internetbanking.card.dto.CardResponseDTO;
import com.internetbanking.card.dto.CardStatusUpdateDTO;

public interface CardWritePlatformService {
    CardResponseDTO createCard(CardRequestDTO cardRequestDTO, String loggedInEmail);
    CardResponseDTO updateCardStatus(Long cardId, CardStatusUpdateDTO statusUpdateDTO, String loggedInEmail);
    void deleteCard(Long cardId, String loggedInEmail);
}