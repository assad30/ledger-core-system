package com.ledger.system.service;

import com.ledger.system.dto.PaymentRequest;
import com.ledger.system.dto.TransactionResponse;

public interface LedgerService {

    TransactionResponse processPayment(PaymentRequest request);
}
