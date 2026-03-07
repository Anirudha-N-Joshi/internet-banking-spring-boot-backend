package com.internetbanking.card.controller;

import com.internetbanking.card.dto.CardRequestDTO;
import com.internetbanking.card.dto.CardResponseDTO;
import com.internetbanking.card.dto.CardStatusUpdateDTO;
import com.internetbanking.card.service.CardReadPlatformService;
import com.internetbanking.card.service.CardWritePlatformService;
import com.internetbanking.common.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final CardReadPlatformService cardReadPlatformService;
    private final CardWritePlatformService cardWritePlatformService;
    private final AuthUtil authUtil;

    public CardController(CardReadPlatformService cardReadPlatformService,
                          CardWritePlatformService cardWritePlatformService,
                          AuthUtil authUtil) {
        this.cardReadPlatformService = cardReadPlatformService;
        this.cardWritePlatformService = cardWritePlatformService;
        this.authUtil = authUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<CardResponseDTO> createCard(@Valid @RequestBody CardRequestDTO cardRequestDTO) {
        String loggedInEmail = authUtil.getLoggedInEmail();
        CardResponseDTO response = cardWritePlatformService.createCard(cardRequestDTO, loggedInEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<List<CardResponseDTO>> getUserCards() {
        String loggedInEmail = authUtil.getLoggedInEmail();
        List<CardResponseDTO> cards = cardReadPlatformService.getUserCards(loggedInEmail);
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{cardId}/status")
    public ResponseEntity<CardResponseDTO> updateCardStatus(@PathVariable Long cardId,
                                                            @Valid @RequestBody CardStatusUpdateDTO statusUpdateDTO) {
        String loggedInEmail = authUtil.getLoggedInEmail();
        CardResponseDTO response = cardWritePlatformService.updateCardStatus(cardId, statusUpdateDTO, loggedInEmail);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long cardId) {
        String loggedInEmail = authUtil.getLoggedInEmail();
        cardWritePlatformService.deleteCard(cardId, loggedInEmail);
        return ResponseEntity.noContent().build();
    }
}