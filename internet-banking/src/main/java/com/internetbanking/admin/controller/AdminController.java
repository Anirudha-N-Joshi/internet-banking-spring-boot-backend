package com.internetbanking.admin.controller;

import com.internetbanking.account.dto.AccountResponseDTO;
import com.internetbanking.account.entity.AccountStatus;
import com.internetbanking.admin.dto.AdminStatsDTO;
import com.internetbanking.admin.service.AdminReadPlatformService;
import com.internetbanking.admin.service.AdminWritePlatformService;
import com.internetbanking.transaction.dto.TransactionResponseDTO;
import com.internetbanking.user.dto.UserResponseDTO;
import com.internetbanking.user.dto.UserStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final AdminReadPlatformService adminReadPlatformService;
    private final AdminWritePlatformService adminWritePlatformService;

    public AdminController(AdminReadPlatformService adminReadPlatformService,
                           AdminWritePlatformService adminWritePlatformService) {
        this.adminReadPlatformService = adminReadPlatformService;
        this.adminWritePlatformService = adminWritePlatformService;
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDTO> getStats() {
        return ResponseEntity.ok(adminReadPlatformService.getStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(adminReadPlatformService.getAllUsers());
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<UserResponseDTO> updateUserStatus(@PathVariable Integer id, @RequestParam UserStatus status) {
        return ResponseEntity.ok(adminWritePlatformService.updateUserStatus(id, status));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        adminWritePlatformService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        return ResponseEntity.ok(adminReadPlatformService.getAllAccounts());
    }

    @PutMapping("/accounts/{id}/status")
    public ResponseEntity<AccountResponseDTO> updateAccountStatus(@PathVariable Long id, @RequestParam AccountStatus status) {
        return ResponseEntity.ok(adminWritePlatformService.updateAccountStatus(id, status));
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions() {
        return ResponseEntity.ok(adminReadPlatformService.getAllTransactions());
    }
}