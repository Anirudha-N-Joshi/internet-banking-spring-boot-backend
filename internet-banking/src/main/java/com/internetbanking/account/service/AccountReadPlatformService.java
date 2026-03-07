package com.internetbanking.account.service;

import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.transaction.dto.AccountLookupDTO;
import com.internetbanking.user.entity.User;

import java.util.List;

public interface AccountReadPlatformService {
    List<AccountResponseDTO> getAccountsByUserId(Integer userId, String loggedInEmail);
    AccountResponseDTO getAccountByNumber(String accountNumber);
    AccountLookupDTO lookupAccount(String accountNumber);
}