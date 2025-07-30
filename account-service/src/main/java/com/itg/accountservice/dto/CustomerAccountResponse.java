package com.itg.accountservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter @Setter
public class CustomerAccountResponse {
    private String customerNumber;
    private String customerName;
    private String mobile;
    private String email;
    private String address1;
    private String address2;
    private Map<String, List<AccountDto>> customerAccounts;
}
