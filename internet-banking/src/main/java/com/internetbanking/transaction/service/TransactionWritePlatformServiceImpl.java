package com.internetbanking.transaction.service;

import com.internetbanking.account.entity.Account;
import com.internetbanking.account.entity.AccountStatus;
import com.internetbanking.account.entity.AccountType;
import com.internetbanking.common.email.EmailTemplates;
import com.internetbanking.common.exception.UnauthorizedException;
import com.internetbanking.account.repository.AccountRepository;
import com.internetbanking.notification.service.MailService;
import com.internetbanking.transaction.dto.TransactionRequestDTO;
import com.internetbanking.transaction.dto.TransactionResponseDTO;
import com.internetbanking.transaction.entity.Transaction;
import com.internetbanking.transaction.entity.TransactionStatus;
import com.internetbanking.transaction.repository.TransactionRepository;
import com.internetbanking.user.entity.User;
import com.internetbanking.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class TransactionWritePlatformServiceImpl implements TransactionWritePlatformService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    public final UserRepository userRepository;
    private final MailService mailService;

    public TransactionWritePlatformServiceImpl(TransactionRepository transactionRepository,
                                               AccountRepository accountRepository, UserRepository userRepository,
                                               MailService mailService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    @Transactional
    public TransactionResponseDTO transfer(TransactionRequestDTO requestDTO, String loggedInEmail) {

        User loggedInUser = userRepository.findByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Account fromAccount = accountRepository.findByAccountNumber(requestDTO.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        if (!fromAccount.getUser().getId().equals(loggedInUser.getId())) {
            throw new UnauthorizedException("You can only transfer from your own account");
        }

        if (fromAccount.getAccountType().equals(AccountType.FIXED_DEPOSIT)) {
            throw new RuntimeException("Transfer cannot done from Fixed Deposit account");
        }

        Account toAccount = accountRepository.findByAccountNumber(requestDTO.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Sender account is not active");
        }
        if (toAccount.getStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Receiver account is not active");
        }
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        if (fromAccount.getBalance().compareTo(requestDTO.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal balanceBefore = fromAccount.getBalance();

        fromAccount.setBalance(fromAccount.getBalance().subtract(requestDTO.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(requestDTO.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionRef(generateTransactionRef());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);
        transaction.setAmount(requestDTO.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(fromAccount.getBalance());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setDescription(requestDTO.getDescription());

        Transaction saved = transactionRepository.save(transaction);

        String[] senderEmail = EmailTemplates.transferSent(
                fromAccount.getUser().getFirstName(), fromAccount.getAccountNumber(), toAccount.getAccountNumber(),
                requestDTO.getAmount(), fromAccount.getBalance(), transaction.getTransactionRef(), transaction.getDescription()
        );
        try {
            mailService.sendSimpleEmail(fromAccount.getUser().getEmail(), senderEmail[0], senderEmail[1]);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send account creation email: ", e );
        }

        String[] receiverEmail = EmailTemplates.transferReceived(
                toAccount.getUser().getFirstName(), fromAccount.getAccountNumber(), toAccount.getAccountNumber(),
                requestDTO.getAmount(), transaction.getTransactionRef(), transaction.getDescription()
        );
        try {
            mailService.sendSimpleEmail(toAccount.getUser().getEmail(), receiverEmail[0], receiverEmail[1]);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send account creation email: ", e );
        }

        if (fromAccount.getBalance().compareTo(new BigDecimal("500")) < 0) {
            String[] email = EmailTemplates.lowBalanceAlert(
                    fromAccount.getUser().getFirstName(), fromAccount.getAccountNumber(), fromAccount.getBalance()
            );
            try {
                mailService.sendSimpleEmail(fromAccount.getUser().getEmail(), email[0], email[1]);
            } catch (MessagingException e) {
                throw new RuntimeException("Failed to send account creation email: ", e );
            }
        }

        return mapToResponseDTO(saved);
    }

    private String generateTransactionRef() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "TXN" + timestamp + random;
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(transaction.getId());
        dto.setTransactionRef(transaction.getTransactionRef());
        dto.setFromAccountNumber(transaction.getFromAccount().getAccountNumber());
        dto.setToAccountNumber(transaction.getToAccount().getAccountNumber());
        dto.setAmount(transaction.getAmount());
        dto.setBalanceBefore(transaction.getBalanceBefore());
        dto.setBalanceAfter(transaction.getBalanceAfter());
        dto.setStatus(transaction.getStatus().name());
        dto.setDescription(transaction.getDescription());
        dto.setFailureReason(transaction.getFailureReason());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}