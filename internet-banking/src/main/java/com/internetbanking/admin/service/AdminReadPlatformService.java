package com.internetbanking.admin.service;

import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.admin.dto.AdminStatsDTO;
import com.internetbanking.transaction.dto.TransactionResponseDTO;
import com.internetbanking.user.dto.UserResponseDTO;

import java.util.List;

public interface AdminReadPlatformService {
    AdminStatsDTO getStats();
    List<UserResponseDTO> getAllUsers();
    List<AccountResponseDTO> getAllAccounts();
    List<TransactionResponseDTO> getAllTransactions();
}