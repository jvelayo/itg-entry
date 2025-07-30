package com.itg.accountservice.model;

import com.itg.accountservice.common.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "account")
@Getter
@Setter
public class Account {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "account_number", nullable = false, unique = true)
   private Long accountNumber;

   @Enumerated(EnumType.STRING)
   @Column(name = "account_type", nullable = false)
   private AccountType accountType;

   private BigDecimal balance;

   private String currency;

   @Column(name = "created_date", nullable = false)
   private LocalDateTime createdDate;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "customer_id", nullable = false)
   private Customer customer;

//   @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//   private List<Card> cardList;
}
