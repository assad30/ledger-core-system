package com.ledger.system.entity;

import com.ledger.system.enums.EntryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_entries")
@Getter
@Setter
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long transactionId;
    private Long accountId;

    @Enumerated(EnumType.STRING)
    private EntryType entryType;

    private BigDecimal amount;

    private LocalDateTime createdAt = LocalDateTime.now();
}
