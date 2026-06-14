package com.ledger.system.service;

import com.ledger.system.dto.PaymentRequest;
import com.ledger.system.dto.RefundRequest;
import com.ledger.system.dto.TransactionResponse;

public interface RefundService {
    TransactionResponse processRefund(RefundRequest request);
}
