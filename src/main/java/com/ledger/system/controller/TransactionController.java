package com.ledger.system.controller;

import com.ledger.system.dto.*;
import com.ledger.system.service.LedgerService;
import com.ledger.system.service.RefundService;
import com.ledger.system.service.SettlementService;
import com.ledger.system.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction APIs")
public class TransactionController {

    private final LedgerService ledgerService;
    private final RefundService refundService;
    private final TransferService transferService;
    private final SettlementService settlementService;

    @Operation(
            summary = "Process Payment",
            description = "Creates a double entry ledger payment transaction"
    )

    @PostMapping("/payment")
    public TransactionResponse payment(@Valid @RequestBody PaymentRequest request) {

        return ledgerService.processPayment(request);

    }

    @PostMapping("/refund")
    public TransactionResponse refund(@Valid @RequestBody RefundRequest request) {
        return refundService.processRefund(request);
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.processTransfer(request);
    }

    @PostMapping("/settlement")
    public TransactionResponse settlement(@Valid @RequestBody SettlementRequest request) {
        return settlementService.settle(request);
    }

}
