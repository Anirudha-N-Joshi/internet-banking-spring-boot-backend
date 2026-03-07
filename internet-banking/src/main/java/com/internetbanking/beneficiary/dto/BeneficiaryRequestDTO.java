package com.internetbanking.beneficiary.dto;

import jakarta.validation.constraints.NotBlank;

public class BeneficiaryRequestDTO {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    private String nickName;

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
}