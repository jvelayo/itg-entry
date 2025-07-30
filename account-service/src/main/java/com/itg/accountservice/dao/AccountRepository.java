package com.itg.accountservice.dao;

import com.itg.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.customer.id = :customerId")
    List<Account> findByCustomerId(Long customerId);

    @Query(value = "SELECT NEXT VALUE FOR account_number_seq", nativeQuery = true)
    Long getNextAccountNumber();

}
