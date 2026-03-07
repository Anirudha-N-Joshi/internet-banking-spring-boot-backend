package com.internetbanking.account.dto;

import com.internetbanking.account.entity.AccountType;
import com.internetbanking.account.entity.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AccountRequestDTO {
    @NotNull(message = "User ID must not be null")
    private Integer userId;

    @NotNull(message = "Account type must be specified")
    private AccountType accountType;

    @NotNull(message = "Currency must be specified")
    private Currency currency;

    @NotNull(message = "Initial deposit is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Initial deposit must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Initial deposit must be a valid monetary amount (max 12 digits and 2 decimal places)")
    private BigDecimal initialDeposit;

    // getters and setters

}