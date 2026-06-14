package com.ledger.system.dto;

import java.math.BigDecimal;

public record SettlementRequest(
                                Long merchantAccountId,
                                BigDecimal amount,
                                String idempotencyKey) {
}
