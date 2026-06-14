package com.ledger.system.service;

import com.ledger.system.dto.SettlementRequest;
import com.ledger.system.dto.TransactionResponse;
import jakarta.validation.Valid;

public interface SettlementService {

    TransactionResponse settle(@Valid SettlementRequest settlementRequest);
}

