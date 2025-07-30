package com.itg.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itg.accountservice.common.enums.AccountType;
import com.itg.accountservice.dto.AccountRequest;
import com.itg.accountservice.dto.AccountResponse;
import com.itg.accountservice.dto.CustomerAccountResponse;
import com.itg.accountservice.exception.ValidationException;
import com.itg.accountservice.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void createAccount_success() throws Exception {
        AccountRequest request = new AccountRequest();
        request.setCustomerName("John Doe");
        request.setAccountType(AccountType.C);
        request.setEmail("john@example.com");
        request.setMobile("09171234567");
        request.setPrimaryAddress("test");
        request.setCurrency("USD");
        request.setInitialBalance(BigDecimal.valueOf(100));

        AccountResponse response  = new AccountResponse();
        response.setAccountNumber("100000");
        response.setCustomerNumber("1");
        response.setAccountType("Checking");

        Mockito.when(accountService.createAccount(any(AccountRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accountNumber").value("100000"))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.success").value(true));
    }
    @Test
    void createAccount_withMissingFields_failed() throws Exception {
        AccountRequest request = new AccountRequest();

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createAccount_withInvalidAccountType_failed() throws Exception {

        String invalidPayload = "{\"accountType\": \"INVALID_TYPE\"}";

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createAccount_withInvalidMobileFormat_failed() throws Exception {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setMobile("1234567");

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.mobile").value("Invalid mobile number format"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createAccount_withInvalidEmailFormat_failed() throws Exception {
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setEmail("test");

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("Invalid email format"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void createAccount_failed() throws Exception {
        AccountRequest request = new AccountRequest();
        request.setCustomerName("John Doe");
        request.setAccountType(AccountType.C);
        request.setEmail("john@example.com");
        request.setMobile("09171234567");
        request.setPrimaryAddress("test");
        request.setCurrency("USD");
        request.setInitialBalance(BigDecimal.valueOf(100));

        Mockito.when(accountService.createAccount(any(AccountRequest.class)))
                .thenThrow(ValidationException.class);

        mockMvc.perform(post("/api/v1/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void getAccounts_success() throws Exception {
        Mockito.when(accountService.getCustomerAccountsByType(any(Long.class)))
                .thenReturn(new CustomerAccountResponse());

        mockMvc.perform(get("/api/v1/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(302));
    }


    @Test
    void getAccounts_notFound() throws Exception {
        Mockito.when(accountService.getCustomerAccountsByType(any(Long.class)))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/api/v1/account/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404));
    }

}