package com.internetbanking.transaction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountLookupDTO {
    private String accountNumber;
    private String accountHolderName;
    private String accountType;

}