package com.itg.accountservice.dto;

import com.itg.accountservice.model.Account;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class AccountDto {
    private String accountNumber;
    private String accountType;
    private BigDecimal availableBalance;
    private String currency;

    public AccountDto(Account account) {
        this.accountNumber = String.valueOf(account.getAccountNumber());
        this.accountType = account.getAccountType().getDisplayName();
        this.availableBalance = account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO;
        this.currency = account.getCurrency();
    }
}
