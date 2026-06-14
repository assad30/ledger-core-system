package com.ledger.system.dto;

import java.util.UUID;

public record TransactionResponse(

        UUID transactionReference,
        String status
) {}
