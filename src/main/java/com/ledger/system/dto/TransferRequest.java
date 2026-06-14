package com.ledger.system.dto;

import java.math.BigDecimal;

public record TransferRequest(

        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String idempotencyKey
)

{ }
