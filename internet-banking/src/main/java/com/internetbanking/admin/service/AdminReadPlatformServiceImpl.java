package com.internetbanking.admin.service;

import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.account.entity.Account;
import com.internetbanking.account.entity.AccountStatus;
import com.internetbanking.account.repository.AccountRepository;
import com.internetbanking.admin.dto.AdminStatsDTO;
import com.internetbanking.transaction.dto.TransactionResponseDTO;
import com.internetbanking.transaction.entity.TransactionStatus;
import com.internetbanking.transaction.repository.TransactionRepository;
import com.internetbanking.user.dto.UserResponseDTO;
import com.internetbanking.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminReadPlatformServiceImpl implements AdminReadPlatformService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AdminReadPlatformServiceImpl(UserRepository userRepository,
                                        AccountRepository accountRepository,
                                        TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public AdminStatsDTO getStats() {
        Long totalUsers = userRepository.count();
        Long totalAccounts = accountRepository.count();
        Long totalTransactions = transactionRepository.count();

        BigDecimal totalMoney = accountRepository.findAll()
                .stream()
                .filter(a -> !a.getStatus().equals(AccountStatus.CLOSED))
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long activeAccounts = accountRepository.countByStatus(AccountStatus.ACTIVE);
        Long suspendedAccounts = accountRepository.countByStatus(AccountStatus.SUSPENDED);
        Long successfulTx = transactionRepository.countByStatus(TransactionStatus.SUCCESS);
        Long failedTx = transactionRepository.countByStatus(TransactionStatus.FAILED);

        return new AdminStatsDTO(totalUsers, totalAccounts, totalTransactions,
                totalMoney, activeAccounts, suspendedAccounts, successfulTx, failedTx);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setId(u.getId());
                    dto.setCreatedDate(u.getCreatedDate());
                    dto.setFirstName(u.getFirstName());
                    dto.setLastName(u.getLastName());
                    dto.setEmail(u.getEmail());
                    dto.setUserName(u.getUserName());
                    dto.setPhoneNumber(u.getPhoneNumber());
                    dto.setAddress(u.getAddress());
                    dto.setRole(u.getRole() != null ? u.getRole().name() : null);
                    dto.setUserStatus(u.getStatus() != null ? u.getStatus() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(a -> {
                    AccountResponseDTO dto = new AccountResponseDTO();
                    dto.setId(a.getId());
                    dto.setAccountNumber(a.getAccountNumber());
                    dto.setAccountType(a.getAccountType() != null ? a.getAccountType().name() : null);
                    dto.setBalance(a.getBalance());
                    dto.setCurrency(a.getCurrency().toString());
                    dto.setStatus(a.getStatus() != null ? a.getStatus().name() : null);
                    dto.setUserId(a.getUser().getId());
                    dto.setCreatedAt(a.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAll()
                .stream()
                .map(t -> {
                    TransactionResponseDTO dto = new TransactionResponseDTO();
                    dto.setId(t.getId());
                    dto.setTransactionRef(t.getTransactionRef());
                    dto.setFromAccountNumber(
                            t.getFromAccount() != null ? t.getFromAccount().getAccountNumber() : null
                    );
                    dto.setToAccountNumber(
                            t.getToAccount() != null ? t.getToAccount().getAccountNumber() : null
                    );
                    dto.setAmount(t.getAmount());
                    dto.setBalanceBefore(t.getBalanceBefore());
                    dto.setBalanceAfter(t.getBalanceAfter());
                    dto.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
                    dto.setDescription(t.getDescription());
                    dto.setFailureReason(t.getFailureReason());
                    dto.setCreatedAt(t.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}