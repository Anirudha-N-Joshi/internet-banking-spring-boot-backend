package com.internetbanking.card.service;

import com.internetbanking.account.entity.Account;
import com.internetbanking.account.repository.AccountRepository;
import com.internetbanking.card.dto.CardRequestDTO;
import com.internetbanking.card.dto.CardResponseDTO;
import com.internetbanking.card.dto.CardStatusUpdateDTO;
import com.internetbanking.card.entity.Card;
import com.internetbanking.card.entity.CardStatus;
import com.internetbanking.card.entity.CardType;
import com.internetbanking.card.exception.CardLimitExceededException;
import com.internetbanking.card.exception.CardNotFoundException;
import com.internetbanking.card.repository.CardRepository;
import com.internetbanking.common.email.EmailTemplates;
import com.internetbanking.common.exception.UnauthorizedException;
import com.internetbanking.notification.service.MailService;
import com.internetbanking.user.entity.User;
import com.internetbanking.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Service
public class CardWritePlatformServiceImpl implements CardWritePlatformService {

    private static final int MAX_CARDS_PER_USER = 5;

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    public CardWritePlatformServiceImpl(CardRepository cardRepository, AccountRepository accountRepository,
                                        UserRepository userRepository, MailService mailService) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public CardResponseDTO createCard(CardRequestDTO requestDTO, String loggedInEmail) {
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // verify account belongs to logged-in user
        Account account = accountRepository.findById(requestDTO.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getUser().getId().equals(loggedInUser.getId())) {
            throw new UnauthorizedException("You can only create cards for your own account");
        }

        // max card limit check
        int existingCards = cardRepository.countByUserId(loggedInUser.getId());
        if (existingCards >= MAX_CARDS_PER_USER) {
            throw new CardLimitExceededException("Maximum card limit of " + MAX_CARDS_PER_USER + " reached");
        }

        // credit card must have credit limit
        if (requestDTO.getCardType() == CardType.CREDIT && requestDTO.getCreditLimit() == null) {
            throw new RuntimeException("Credit limit is required for credit cards");
        }

        Card card = new Card();
        card.setCardNumber(generateCardNumber());
        card.setCvv(generateCvv());
        card.setCardHolderName(loggedInUser.getFirstName() + " " + loggedInUser.getLastName());
        card.setCardType(requestDTO.getCardType());
        card.setStatus(CardStatus.ACTIVE);
        card.setExpiryDate(LocalDate.now().plusYears(5));  // 5 year validity
        card.setUser(loggedInUser);
        card.setLinkedAccount(account);

        // set credit fields only for credit cards
        if (requestDTO.getCardType() == CardType.CREDIT) {
            card.setCreditLimit(requestDTO.getCreditLimit());
            card.setCurrentBalance(BigDecimal.ZERO);
            card.setAvailableCredit(requestDTO.getCreditLimit());
        }

        Card saved = cardRepository.save(card);

        String[] email = EmailTemplates.cardCreation(
                loggedInUser.getFirstName(), card.getCardNumber(),
                card.getCardType().name(), card.getExpiryDate(), card.getCreditLimit()
        );
        try {
            mailService.sendSimpleEmail(loggedInUser.getEmail(), email[0], email[1]);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send account creation email: ", e );
        }

        return mapToResponseDTO(saved);
    }

    @Override
    public CardResponseDTO updateCardStatus(Long cardId, CardStatusUpdateDTO statusUpdateDTO, String loggedInEmail) {
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getId().equals(loggedInUser.getId())) {
            throw new UnauthorizedException("You can only update your own cards");
        }

        CardStatus oldStatus = card.getStatus();

        // cannot reactivate a cancelled or expired card
        if (card.getStatus() == CardStatus.CANCELLED || card.getStatus() == CardStatus.EXPIRED) {
            throw new RuntimeException("Cannot update status of a " + card.getStatus().name().toLowerCase() + " card");
        }

        card.setStatus(statusUpdateDTO.getStatus());
        Card updated = cardRepository.save(card);

        String[] email = EmailTemplates.cardStatusUpdate(
                loggedInUser.getFirstName(), card.getCardNumber(), oldStatus.toString(), card.getStatus().toString()
        );

        try {
            mailService.sendSimpleEmail(loggedInUser.getEmail(), email[0], email[1]);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send account creation email: ", e );
        }

        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteCard(Long cardId, String loggedInEmail) {
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));

        if (!card.getUser().getId().equals(loggedInUser.getId())) {
            throw new UnauthorizedException("You can only delete your own cards");
        }

        // soft delete — mark as cancelled instead of removing record
        card.setStatus(CardStatus.CANCELLED);
        cardRepository.save(card);
    }

    private String generateCardNumber() {
        // generates 16 digit card number
        Random random = new Random();
        StringBuilder cardNumber = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }
        return cardNumber.toString();
    }

    private String generateCvv() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }

    private CardResponseDTO mapToResponseDTO(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setId(card.getId());
        dto.setMaskedCardNumber("**** **** **** " + card.getCardNumber().substring(card.getCardNumber().length() - 4));
        dto.setCardHolderName(card.getCardHolderName());
        dto.setCardType(card.getCardType().name());
        dto.setStatus(card.getStatus().name());
        dto.setExpiryDate(card.getExpiryDate());
        dto.setAccountId(card.getLinkedAccount().getId());
        dto.setUserId(card.getUser().getId());
        dto.setCreatedAt(card.getCreatedAt());

        if (card.getCardType() == CardType.CREDIT) {
            dto.setCreditLimit(card.getCreditLimit());
            dto.setCurrentBalance(card.getCurrentBalance());
            dto.setAvailableCredit(card.getAvailableCredit());
        }

        return dto;
    }
}