package com.internetbanking.card.service;

import com.internetbanking.card.dto.CardResponseDTO;
import com.internetbanking.card.entity.Card;
import com.internetbanking.card.repository.CardRepository;
import com.internetbanking.user.entity.User;
import com.internetbanking.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardReadPlatformServiceImpl implements CardReadPlatformService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public CardReadPlatformServiceImpl(CardRepository cardRepository,
                                       UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<CardResponseDTO> getUserCards(String loggedInEmail) {
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cardRepository.findByUserId(loggedInUser.getId())
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private CardResponseDTO mapToResponseDTO(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setId(card.getId());
        dto.setMaskedCardNumber(maskCardNumber(card.getCardNumber()));
        dto.setCardHolderName(card.getCardHolderName());
        dto.setCardType(card.getCardType().name());
        dto.setStatus(card.getStatus().name());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setAccountId(card.getLinkedAccount().getId());
        dto.setUserId(card.getUser().getId());
        dto.setCreatedAt(card.getCreatedAt());

        // only populate credit fields for CREDIT cards
        if (card.getCardType().name().equals("CREDIT")) {
            dto.setCreditLimit(card.getCreditLimit());
            dto.setCurrentBalance(card.getCurrentBalance());
            dto.setAvailableCredit(card.getAvailableCredit());
        }

        return dto;
    }

    private String maskCardNumber(String cardNumber) {
        // converts 1234567890123456 → **** **** **** 3456
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}