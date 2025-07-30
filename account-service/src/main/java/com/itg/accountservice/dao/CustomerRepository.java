package com.itg.accountservice.dao;

import com.itg.accountservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    boolean existsByEmail(String email);
    boolean existsByMobile(String mobile);
}
