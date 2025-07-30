package com.itg.accountservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AccountResponse {
    private String accountNumber;
    private String accountType;
    private String customerNumber;
}
