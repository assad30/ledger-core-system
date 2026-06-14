package com.ledger.system.service;

import com.ledger.system.dto.TransactionResponse;
import com.ledger.system.dto.TransferRequest;

public interface TransferService {

    TransactionResponse processTransfer(TransferRequest transferRequest);
}
