package com.internetbanking.account.service;

import com.internetbanking.account.dto.AccountRequestDTO;
import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.account.entity.Account;
import com.internetbanking.account.entity.AccountStatus;
import com.internetbanking.account.exception.AccountNotFoundException;
import com.internetbanking.account.repository.AccountRepository;
import com.internetbanking.common.email.EmailTemplates;
import com.internetbanking.notification.service.MailService;
import com.internetbanking.user.entity.User;
import com.internetbanking.user.exception.UserNotFoundException;
import com.internetbanking.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Logger;

@Service
public class AccountWritePlatformServiceImpl implements AccountWritePlatformService {

    private final AccountRepository accountRepository;
    private final MailService mailService;
    private final UserRepository userRepository;

    public AccountWritePlatformServiceImpl(AccountRepository accountRepository, MailService mailService, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.mailService = mailService;
        this.userRepository = userRepository;
    }

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        User user = userRepository.findById(accountRequestDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with this ID not found"));

        Account account = new Account();
        account.setUser(user);
        account.setAccountType(accountRequestDTO.getAccountType());
        account.setCurrency(accountRequestDTO.getCurrency());
        account.setBalance(accountRequestDTO.getInitialDeposit());
        account.setStatus(AccountStatus.ACTIVE);
        account.setAccountNumber(generateAccountNumber());

        Account savedAccount = accountRepository.save(account);

        String[] email = EmailTemplates.accountCreation(
                user.getFirstName(), account.getAccountNumber(),
                account.getAccountType().name(), account.getCurrency().name(), account.getBalance().toString()
        );
        try {
            mailService.sendSimpleEmail(user.getEmail(), email[0], email[1]);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send account creation email: ", e );
        }

        return mapToResponseDTO(savedAccount);
    }

    @Override
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));

        account.setStatus(AccountStatus.CLOSED); // soft delete
        accountRepository.save(account);
    }

    private String generateAccountNumber() {
        String accountNumber;
        do {
            accountNumber = "ACC" + UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 10)
                    .toUpperCase();

        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        return accountNumber;
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