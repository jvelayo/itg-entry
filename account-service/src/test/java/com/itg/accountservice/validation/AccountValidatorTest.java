package com.itg.accountservice.validation;

import com.itg.accountservice.dao.CustomerRepository;
import com.itg.accountservice.dto.AccountRequest;
import com.itg.accountservice.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class AccountValidatorTest {

    @InjectMocks
    AccountValidator accountValidator;

    @Mock
    CustomerRepository customerRepository;

    private AccountRequest accountRequest;

    @BeforeEach
    void setup (){
        accountRequest = new AccountRequest();
        accountRequest.setMobile("12345678910");
        accountRequest.setEmail("test@mail.com");
    }

    @Test
    void validate_emailAndMobileDoNotExists() {
        Mockito.when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        Mockito.when(customerRepository.existsByMobile(anyString())).thenReturn(false);

        accountValidator.validate(accountRequest);
        Mockito.verify(customerRepository).existsByEmail(anyString());
        Mockito.verify(customerRepository).existsByMobile(anyString());
    }

    @Test
    void validate_emailExists() {
        Mockito.when(customerRepository.existsByEmail(anyString())).thenReturn(true);

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            accountValidator.validate(accountRequest);
        });

        assertEquals(validationException.getMessage(), "Email already in used.");
    }

    @Test
    void validate_mobileExists() {
        Mockito.when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        Mockito.when(customerRepository.existsByMobile(anyString())).thenReturn(true);

        ValidationException validationException = assertThrows(ValidationException.class, () -> {
            accountValidator.validate(accountRequest);
        });
        assertEquals(validationException.getMessage(), "Mobile already in used.");
    }

}