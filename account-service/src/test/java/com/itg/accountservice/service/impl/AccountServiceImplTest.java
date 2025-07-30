package com.itg.accountservice.service.impl;

import com.itg.accountservice.common.enums.AccountType;
import com.itg.accountservice.dao.AccountRepository;
import com.itg.accountservice.dao.CustomerRepository;
import com.itg.accountservice.dto.AccountRequest;
import com.itg.accountservice.dto.AccountResponse;

import com.itg.accountservice.dto.CustomerAccountResponse;
import com.itg.accountservice.exception.ValidationException;
import com.itg.accountservice.model.Account;
import com.itg.accountservice.model.Customer;
import com.itg.accountservice.validation.AccountValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import javax.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;


@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    private static final String USD = "USD";
    @InjectMocks
    private AccountServiceImpl accountService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountValidator accountValidator;

    @Test
    void createAccount_success() {
        AccountRequest request = new AccountRequest();
        request.setAccountType(AccountType.C);

        Customer customer = new Customer();
        customer.setId(1L);

        Account savedAccount = new Account();
        savedAccount.setAccountType(AccountType.C);
        savedAccount.setAccountNumber(1000L);

        Mockito.doNothing().when(accountValidator).validate(any(AccountRequest.class));

        Mockito.when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        Mockito.when(accountRepository.save(any(Account.class))).thenReturn(savedAccount);
        Mockito.when(accountRepository.getNextAccountNumber()).thenReturn(1000L);

        AccountResponse response = accountService.createAccount(request);

        Mockito.verify(accountValidator).validate(request);
        assertEquals(response.getAccountNumber(),"1000");
        assertEquals(response.getCustomerNumber(),"1");
    }

    @Test
    void createAccount_emailExists_failed() throws Exception{
        AccountRequest request = new AccountRequest();
        request.setAccountType(AccountType.C);

        Mockito.doThrow(new ValidationException("Email already in used"))
                .when(accountValidator)
                .validate(any(AccountRequest.class));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            accountService.createAccount(request);
        });

        assertEquals("Email already in used", exception.getMessage());
    }

    @Test
    void createAccount_mobileExists_failed() throws Exception{
        AccountRequest request = new AccountRequest();
        request.setAccountType(AccountType.C);

        Mockito.doThrow(new ValidationException("Mobile already in used"))
                .when(accountValidator)
                .validate(any(AccountRequest.class));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            accountService.createAccount(request);
        });

        assertEquals("Mobile already in used", exception.getMessage());
    }

    @Test
    void getCustomerAccountsByType_success() {
        Customer customer = new Customer();
        customer.setId(1L);

        Account checking = new Account();
        checking.setAccountNumber(1000L);
        checking.setAccountType(AccountType.C);
        checking.setCurrency(USD);
        checking.setBalance(BigDecimal.ZERO);

        Account savings = new Account();
        savings.setAccountNumber(1001L);
        savings.setAccountType(AccountType.S);
        savings.setCurrency(USD);
        savings.setBalance(BigDecimal.valueOf(100L));

        List<Account> accounts = new ArrayList<>();
        accounts.add(checking);
        accounts.add(savings);

        Mockito.when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        Mockito.when(accountRepository.findByCustomerId(anyLong())).thenReturn(accounts);

        CustomerAccountResponse response = accountService.getCustomerAccountsByType(1L);

        assertEquals(response.getCustomerNumber(), String.valueOf(1L));
        assertEquals(response.getCustomerAccounts().keySet().size(), accounts.size());
        assertEquals(response.getCustomerAccounts().get(AccountType.C.getDisplayName())
                .get(0).getAccountNumber(), String.valueOf(1000L));
        assertEquals(response.getCustomerAccounts().get(AccountType.S.getDisplayName())
                .get(0).getAccountNumber(), String.valueOf(1001L));
    }

    @Test
    void getCustomerAccountsByType_customerNotFound_failed() {

        Mockito.when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            accountService.getCustomerAccountsByType(1L);
        });

    }


}