package com.internetbanking.transaction.repository;

import com.internetbanking.transaction.entity.Transaction;
import com.internetbanking.transaction.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE " +
            "t.fromAccount.id = :accountId OR t.toAccount.id = :accountId " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findAllByAccountId(@Param("accountId") Long accountId);

    List<Transaction> findByFromAccount_IdOrderByCreatedAtDesc(Long accountId);

    List<Transaction> findByToAccount_IdOrderByCreatedAtDesc(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE " +
            "(t.fromAccount.id = :accountId OR t.toAccount.id = :accountId) " +
            "AND t.createdAt BETWEEN :from AND :to " +
            "ORDER BY t.createdAt DESC")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId,
            @Param("from") LocalDateTime from, @Param("to") LocalDateTime to
    );

    Optional<Transaction> findByTransactionRef(String transactionRef);

    Long countByStatus(TransactionStatus status);
}