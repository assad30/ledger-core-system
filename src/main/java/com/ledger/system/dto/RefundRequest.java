package com.ledger.system.dto;

import java.math.BigDecimal;

public record RefundRequest(

                            Long originalTransactionId,
                            BigDecimal amount,
                            String idempotencyKey)
{ }
