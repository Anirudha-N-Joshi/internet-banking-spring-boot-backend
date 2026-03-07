package com.internetbanking.transaction.service;

import com.internetbanking.transaction.dto.TransactionRequestDTO;
import com.internetbanking.transaction.dto.TransactionResponseDTO;

public interface TransactionWritePlatformService {
    TransactionResponseDTO transfer(TransactionRequestDTO requestDTO, String loggedInEmail);
}