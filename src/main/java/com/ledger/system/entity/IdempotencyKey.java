package com.ledger.system.entity;


import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "idempotency_keys", uniqueConstraints = {
@UniqueConstraint(name = "uk_idempotency_key",columnNames = "key_value")})
@Getter
@Setter
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_value", nullable = false)
    private String keyValue;

    @Column(name = "transaction_reference")
    private UUID transactionReference;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private String response;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

}
