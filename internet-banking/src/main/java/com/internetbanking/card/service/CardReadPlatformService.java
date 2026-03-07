package com.internetbanking.card.service;

import com.internetbanking.card.dto.CardResponseDTO;
import java.util.List;

public interface CardReadPlatformService {
    List<CardResponseDTO> getUserCards(String loggedInEmail);
}