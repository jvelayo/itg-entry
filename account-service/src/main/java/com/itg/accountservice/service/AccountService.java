package com.itg.accountservice.service;

import com.itg.accountservice.dto.AccountRequest;
import com.itg.accountservice.dto.AccountResponse;
import com.itg.accountservice.dto.CustomerAccountResponse;
import org.springframework.stereotype.Service;

public interface AccountService {

    AccountResponse createAccount(AccountRequest accountRequest);
    CustomerAccountResponse getCustomerAccountsByType(Long customerId);
}
