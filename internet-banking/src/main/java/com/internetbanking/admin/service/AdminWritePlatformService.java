package com.internetbanking.admin.service;

import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.account.entity.AccountStatus;
import com.internetbanking.user.dto.UserResponseDTO;
import com.internetbanking.user.dto.UserStatus;

public interface AdminWritePlatformService {
    UserResponseDTO updateUserStatus(Integer userId, UserStatus status);
    void deleteUser(Integer userId);
    AccountResponseDTO updateAccountStatus(Long accountId, AccountStatus status);
}