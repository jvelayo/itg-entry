package com.itg.accountservice.controller;

import com.itg.accountservice.common.ApiBaseResponse;
import com.itg.accountservice.common.MessageConstants;
import com.itg.accountservice.dto.AccountRequest;
import com.itg.accountservice.dto.AccountResponse;
import com.itg.accountservice.dto.CustomerAccountResponse;

import com.itg.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/v1/account")
@Tag(name = "Accounts", description = "Operations related to accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping(consumes = "application/json")
    @Operation(summary = "Create new account")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        logger.info("Creating account for customer : {} ", accountRequest.getCustomerName());
        AccountResponse created = accountService.createAccount(accountRequest);
        return new ResponseEntity<>(ApiBaseResponse.created(created, MessageConstants.ACCOUNT_CREATED), HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer accounts")
    public ResponseEntity<?> getAccounts(@PathVariable Long customerId) {
        logger.info("Fetching accounts for customer : {} ", customerId);
        CustomerAccountResponse response = accountService.getCustomerAccountsByType(customerId);
        return ResponseEntity.ok(ApiBaseResponse.found(response, MessageConstants.ACCOUNT_FOUND));
    }

}
