package com.internetbanking.admin.dto;

import java.math.BigDecimal;

public class AdminStatsDTO {
    private final Long totalUsers;
    private final Long totalAccounts;
    private final Long totalTransactions;
    private final BigDecimal totalMoneyInSystem;
    private final Long activeAccounts;
    private final Long suspendedAccounts;
    private final Long successfulTransactions;
    private final Long failedTransactions;

    public AdminStatsDTO(Long totalUsers, Long totalAccounts, Long totalTransactions,
                         BigDecimal totalMoneyInSystem, Long activeAccounts,
                         Long suspendedAccounts, Long successfulTransactions, Long failedTransactions) {
        this.totalUsers = totalUsers;
        this.totalAccounts = totalAccounts;
        this.totalTransactions = totalTransactions;
        this.totalMoneyInSystem = totalMoneyInSystem;
        this.activeAccounts = activeAccounts;
        this.suspendedAccounts = suspendedAccounts;
        this.successfulTransactions = successfulTransactions;
        this.failedTransactions = failedTransactions;
    }

    // Getters
    public Long getTotalUsers() { return totalUsers; }
    public Long getTotalAccounts() { return totalAccounts; }
    public Long getTotalTransactions() { return totalTransactions; }
    public BigDecimal getTotalMoneyInSystem() { return totalMoneyInSystem; }
    public Long getActiveAccounts() { return activeAccounts; }
    public Long getSuspendedAccounts() { return suspendedAccounts; }
    public Long getSuccessfulTransactions() { return successfulTransactions; }
    public Long getFailedTransactions() { return failedTransactions; }
}