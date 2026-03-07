package com.internetbanking.card.repository;

import com.internetbanking.card.entity.Card;
import com.internetbanking.card.entity.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUserId(Integer userId);
    List<Card> findByUserIdAndStatus(Long userId, CardStatus status);
    Optional<Card> findByCardNumber(String cardNumber);
//    boolean existsByAccountIdAndCardType(Long accountId, String cardType);
    int countByUserId(Integer userId);
}