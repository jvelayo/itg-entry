package com.itg.accountservice.validation;

import com.itg.accountservice.common.MessageConstants;
import com.itg.accountservice.dao.CustomerRepository;
import com.itg.accountservice.dto.AccountRequest;
import com.itg.accountservice.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountValidator {

    private static final Logger logger = LoggerFactory.getLogger(AccountValidator.class);
    @Autowired
    protected CustomerRepository customerRepository;

    public void validate(AccountRequest accountRequest) {
        checkEmailAndContactNumber(accountRequest.getEmail(), accountRequest.getMobile());
        doTypeSpecificValidation(accountRequest);
    }

    private void checkEmailAndContactNumber(String email, String mobile) {
        logger.debug("Checking email: {}, mobile: {}", email, mobile);
        if (customerRepository.existsByEmail(email)) {
            throw new ValidationException(MessageConstants.INVALID_EMAIL_EXISTS);
        }
        if (customerRepository.existsByMobile(mobile)) {
            throw new ValidationException(MessageConstants.INVALID_MOBILE_EXISTS);
        }
    }
    // type-specific checks here
    public  void doTypeSpecificValidation(AccountRequest request) {
        logger.debug("Default account type validation — extend this later.");
    }
}
