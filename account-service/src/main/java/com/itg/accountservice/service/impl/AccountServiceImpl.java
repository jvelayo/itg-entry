package com.itg.accountservice.service.impl;

import com.itg.accountservice.common.MessageConstants;
import com.itg.accountservice.dao.AccountRepository;
import com.itg.accountservice.dao.CustomerRepository;
import com.itg.accountservice.dto.AccountDto;
import com.itg.accountservice.dto.AccountRequest;
import com.itg.accountservice.dto.AccountResponse;
import com.itg.accountservice.dto.CustomerAccountResponse;
import com.itg.accountservice.model.Account;
import com.itg.accountservice.model.Customer;
import com.itg.accountservice.service.AccountService;
import com.itg.accountservice.validation.AccountValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountValidator accountValidator;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository, AccountValidator accountValidator) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.accountValidator = accountValidator;
    }

    @Transactional
    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {
        logger.info("Creating account for customer: {}", accountRequest.getCustomerName());
        validateRequest(accountRequest);
        logger.debug("Validation passed. Creating new customer and account...");
        Customer customer = new Customer();
        customer.setName(accountRequest.getCustomerName());
        customer.setEmail(accountRequest.getEmail());
        customer.setMobile(accountRequest.getMobile());
        customer.setPrimaryAddress(accountRequest.getPrimaryAddress());
        customer.setSecondaryAddress(accountRequest.getSecondaryAddress());
        customer.setCreatedDate(LocalDateTime.now());
        Customer newCustomer = customerRepository.save(customer);


        Account account = new Account();
        account.setAccountNumber(accountRepository.getNextAccountNumber());
        account.setAccountType(accountRequest.getAccountType());
        account.setBalance(accountRequest.getInitialBalance());
        account.setCreatedDate(LocalDateTime.now());
        account.setCustomer(newCustomer);
        account.setCurrency(accountRequest.getCurrency());

        Account newAccount =  accountRepository.save(account);

        AccountResponse response = new AccountResponse();
        response.setAccountNumber(String.valueOf(newAccount.getAccountNumber()));
        response.setAccountType(newAccount.getAccountType().getDisplayName());
        response.setCustomerNumber(String.valueOf(account.getCustomer().getId()));
        logger.info("Successfully created account number: {} for customer ID: {}",
                newAccount.getAccountNumber(), newCustomer.getId());
        return response;
    }

    @Override
    public CustomerAccountResponse getCustomerAccountsByType(Long customerId) {
        logger.info("Fetching accounts for customer ID: {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.CUSTOMER_NOT_FOUND));
        List<Account> accountAll = accountRepository.findAll();

        List<Account> accounts = accountRepository.findByCustomerId(customerId);
        logger.debug("Found {} account(s) for customer ID: {}", accounts.size(), customerId);

        Map<String, List<AccountDto>> acccountsMap = accounts.stream()
                .map(AccountDto::new)
                .collect(Collectors.groupingBy(AccountDto::getAccountType));

        CustomerAccountResponse accountResponse = new CustomerAccountResponse();
        accountResponse.setCustomerName(customer.getName());
        accountResponse.setCustomerNumber(String.valueOf(customer.getId()));
        accountResponse.setEmail(customer.getEmail());
        accountResponse.setMobile(customer.getMobile());
        accountResponse.setAddress1(customer.getPrimaryAddress());
        accountResponse.setAddress2(customer.getSecondaryAddress());
        accountResponse.setCustomerAccounts(acccountsMap);

        return accountResponse;
    }

    private void validateRequest(AccountRequest accountRequest) {
        logger.debug("Validating account request for email: {}", accountRequest.getEmail());
        accountValidator.validate(accountRequest);
    }
}
