package com.internetbanking.transaction.service;

import com.internetbanking.transaction.dto.TransactionResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface TransactionReadPlatformService {
    List<TransactionResponseDTO> getAccountTransactions(Long accountId);
    List<TransactionResponseDTO> getOutgoingTransactions(Long accountId);
    List<TransactionResponseDTO> getIncomingTransactions(Long accountId);
    public byte[] generateStatement(Long accountId, LocalDate from, LocalDate to);
}