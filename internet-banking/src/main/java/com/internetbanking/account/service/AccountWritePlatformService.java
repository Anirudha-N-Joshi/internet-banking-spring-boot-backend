package com.internetbanking.account.service;

import com.internetbanking.account.dto.AccountRequestDTO;
import com.internetbanking.account.dto.AccountResponseDTO;

public interface AccountWritePlatformService {
    AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO);
    void deleteAccount(Long accountId);
}