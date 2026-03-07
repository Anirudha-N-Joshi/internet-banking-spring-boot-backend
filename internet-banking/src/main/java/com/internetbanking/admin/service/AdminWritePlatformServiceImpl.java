package com.internetbanking.admin.service;

import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.account.entity.Account;
import com.internetbanking.account.entity.AccountStatus;
import com.internetbanking.account.repository.AccountRepository;
import com.internetbanking.user.dto.UserResponseDTO;
import com.internetbanking.user.dto.UserStatus;
import com.internetbanking.user.entity.User;
import com.internetbanking.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AdminWritePlatformServiceImpl implements AdminWritePlatformService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AdminWritePlatformServiceImpl(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public UserResponseDTO updateUserStatus(Integer userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        User saved = userRepository.save(user);

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(saved.getId());
        dto.setFirstName(saved.getFirstName());
        dto.setLastName(saved.getLastName());
        dto.setEmail(saved.getEmail());
        dto.setUserName(saved.getUserName());
        dto.setPhoneNumber(saved.getPhoneNumber());
        dto.setAddress(saved.getAddress());
        dto.setRole(saved.getRole() != null ? saved.getRole().name() : null);
        dto.setUserStatus(saved.getStatus() != null ? saved.getStatus() : null);
        return dto;
    }

    @Override
    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public AccountResponseDTO updateAccountStatus(Long accountId, AccountStatus status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setStatus(status);
        Account saved = accountRepository.save(account);

        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(saved.getId());
        dto.setAccountNumber(saved.getAccountNumber());
        dto.setAccountType(saved.getAccountType() != null ? saved.getAccountType().name() : null);
        dto.setBalance(saved.getBalance());
        dto.setCurrency(saved.getCurrency().toString());
        dto.setStatus(saved.getStatus() != null ? saved.getStatus().name() : null);
        dto.setUserId(saved.getUser().getId());
        dto.setCreatedAt(saved.getCreatedAt());
        return dto;
    }
}