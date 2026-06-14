package com.ledger.system.entity;

import com.ledger.system.enums.AccountType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountNumber;
    private String accountName;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private String currency;
}
