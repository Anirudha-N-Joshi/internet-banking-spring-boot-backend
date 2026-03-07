package com.internetbanking.transaction.controller;

import com.internetbanking.common.AuthUtil;
import com.internetbanking.transaction.dto.TransactionRequestDTO;
import com.internetbanking.transaction.dto.TransactionResponseDTO;
import com.internetbanking.transaction.service.TransactionReadPlatformService;
import com.internetbanking.transaction.service.TransactionWritePlatformService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionReadPlatformService transactionReadPlatformService;
    private final TransactionWritePlatformService transactionWritePlatformService;
    private final AuthUtil authUtil;

    public TransactionController(TransactionReadPlatformService transactionReadPlatformService,
                                 TransactionWritePlatformService transactionWritePlatformService, AuthUtil authUtil) {
        this.transactionReadPlatformService = transactionReadPlatformService;
        this.transactionWritePlatformService = transactionWritePlatformService;
        this.authUtil = authUtil;
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransactionRequestDTO requestDTO) {
        String loggedInEmail = authUtil.getLoggedInEmail();
        TransactionResponseDTO response = transactionWritePlatformService.transfer(requestDTO, loggedInEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getAccountTransactions(@PathVariable Long accountId) {
        List<TransactionResponseDTO> transactions = transactionReadPlatformService.getAccountTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/outgoing/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getOutgoingTransactions(@PathVariable Long accountId) {
        List<TransactionResponseDTO> transactions = transactionReadPlatformService.getOutgoingTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/incoming/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getIncomingTransactions(@PathVariable Long accountId) {
        List<TransactionResponseDTO> transactions = transactionReadPlatformService.getIncomingTransactions(accountId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{accountId}/statement")
    public ResponseEntity<byte[]> getStatement(@PathVariable Long accountId,
            @RequestParam LocalDate from, @RequestParam LocalDate to) {

        byte[] pdf = transactionReadPlatformService.generateStatement(accountId, from, to);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=statement.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}