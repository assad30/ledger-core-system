package com.ledger.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record PaymentRequest(

        @NotNull Long userAccountId,
        @NotNull Long merchantAccountId,
        @NotNull BigDecimal amount,
        @PositiveOrZero BigDecimal fee,
        @NotBlank String idempotencyKey

) { }
