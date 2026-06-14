package com.ledger.system.entity;

import com.ledger.system.enums.TransactionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ledger_transactions")
@Getter
@Setter
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID transactionReference;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    private Long parentTransactionId;

    @Column(unique = true)
    private String idempotencyKey;

    private LocalDateTime createdAt = LocalDateTime.now();
}
