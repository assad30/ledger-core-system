package com.ledger.system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyValue;

    private LocalDateTime createdAt = LocalDateTime.now();
}
