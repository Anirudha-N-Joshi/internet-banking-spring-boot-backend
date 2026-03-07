package com.internetbanking.account.controller;

import com.internetbanking.account.dto.AccountRequestDTO;
import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.account.service.AccountReadPlatformService;
import com.internetbanking.account.service.AccountWritePlatformService;
import com.internetbanking.common.AuthUtil;
import com.internetbanking.transaction.dto.AccountLookupDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountReadPlatformService accountReadPlatformService;
    private final AccountWritePlatformService accountWritePlatformService;
    private final AuthUtil authUtil;

    public AccountController(AccountReadPlatformService accountReadPlatformService,
                             AccountWritePlatformService accountWritePlatformService, AuthUtil authUtil) {
        this.accountReadPlatformService = accountReadPlatformService;
        this.accountWritePlatformService = accountWritePlatformService;
        this.authUtil = authUtil;
    }

    @PostMapping("/create")
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        AccountResponseDTO response = accountWritePlatformService.createAccount(accountRequestDTO);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByUser(@PathVariable Integer userId) {
        String loggedInEmail = authUtil.getLoggedInEmail();
        List<AccountResponseDTO> accounts = accountReadPlatformService.getAccountsByUserId(userId, loggedInEmail);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByNumber(@PathVariable String accountNumber) {
        AccountResponseDTO account = accountReadPlatformService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountWritePlatformService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountNumber}/lookup")
    public ResponseEntity<AccountLookupDTO> lookupAccount(@PathVariable String accountNumber) {
        AccountLookupDTO lookup = accountReadPlatformService.lookupAccount(accountNumber);
        return ResponseEntity.ok(lookup);
    }
}