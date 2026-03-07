package com.internetbanking.account.service;

import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.account.entity.Account;
import com.internetbanking.account.exception.AccountNotFoundException;
import com.internetbanking.common.exception.UnauthorizedException;
import com.internetbanking.account.repository.AccountRepository;
import com.internetbanking.transaction.dto.AccountLookupDTO;
import com.internetbanking.user.entity.User;
import com.internetbanking.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountReadPlatformServiceImpl implements AccountReadPlatformService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public AccountReadPlatformServiceImpl(AccountRepository accountRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AccountResponseDTO> getAccountsByUserId(Integer userId, String loggedInEmail) {
        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!loggedInUser.getId().equals(userId)) {
            throw new UnauthorizedException("You can only view your own accounts");
        }

        return accountRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    public AccountResponseDTO getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        return mapToResponseDTO(account);
    }

    @Override
    public AccountLookupDTO lookupAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        User user = userRepository.findById(account.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found for account: " + accountNumber));

        AccountLookupDTO dto = new AccountLookupDTO();
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountHolderName(user.getFirstName() + " " + user.getLastName());
        dto.setAccountType(account.getAccountType().name());

        return dto;
    }

    private AccountResponseDTO mapToResponseDTO(Account account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setAccountNumber(account.getAccountNumber());
        dto.setAccountType(account.getAccountType().name());
        dto.setBalance(account.getBalance());
        dto.setCurrency(account.getCurrency().name());
        dto.setStatus(account.getStatus().name());
        dto.setUserId(account.getUser().getId());
        dto.setCreatedAt(account.getCreatedAt());
        return dto;
    }
}