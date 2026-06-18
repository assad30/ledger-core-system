package com.ledger.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledger.system.dto.PaymentRequest;
import com.ledger.system.dto.TransactionResponse;
import com.ledger.system.entity.*;
import com.ledger.system.enums.EntryType;
import com.ledger.system.enums.TransactionType;
import com.ledger.system.repository.*;
import com.ledger.system.service.LedgerService;
import com.ledger.system.validation.LedgerValidationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final AccountRepository accountRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final LedgerEntryRepository entryRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final ObjectMapper objectMapper;
    private final LedgerValidationService validationService;

    private static final Long FEES_ACCOUNT_ID = 3L;

    @Override
    @Transactional
    public TransactionResponse processPayment(PaymentRequest request) {


        var existingOpt = idempotencyRepository.findByKeyValue(request.idempotencyKey());

        if (existingOpt.isPresent()) {
            IdempotencyKey existing = existingOpt.get();
            if ("SUCCESS".equals(existing.getStatus())) {
                try {
                    return objectMapper.readValue(existing.getResponse(),TransactionResponse.class);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse cached response", e);
                }
            }

            throw new RuntimeException("Request already processing");
        }

        IdempotencyKey pending = new IdempotencyKey();
        pending.setKeyValue(request.idempotencyKey());
        pending.setStatus("PENDING");
        pending.setCreatedAt(LocalDateTime.now());
        idempotencyRepository.save(pending);


        validationService.validatePayment(request.amount(), request.fee(), request.userAccountId(),request.merchantAccountId());


        Account user = accountRepository.lockAccount(request.userAccountId());
        Account merchant = accountRepository.lockAccount(request.merchantAccountId());

        BigDecimal total = request.amount();
        BigDecimal fee = request.fee();
        BigDecimal merchantAmount = total.subtract(fee);


        LedgerTransaction tx = new LedgerTransaction();
        tx.setTransactionReference(UUID.randomUUID());
        tx.setTransactionType(TransactionType.PAYMENT);
        tx.setIdempotencyKey(request.idempotencyKey());

        tx = transactionRepository.save(tx);

        Long txId = tx.getId();

        createEntry(txId, user.getId(), EntryType.DEBIT, total);
        createEntry(txId, merchant.getId(), EntryType.CREDIT, merchantAmount);
        createEntry(txId, FEES_ACCOUNT_ID, EntryType.CREDIT, fee);

        validateBalanced(txId);


        TransactionResponse response = new TransactionResponse(tx.getTransactionReference(), "SUCCESS");

        // =========================
        // 8. UPDATE IDEMPOTENCY
        // =========================
        IdempotencyKey record = idempotencyRepository.findByKeyValue(request.idempotencyKey())
                        .orElseThrow();

        record.setStatus("SUCCESS");
        record.setTransactionReference(tx.getTransactionReference());

        try {
            record.setResponse(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            throw new RuntimeException("Serialization failed", e);
        }
        idempotencyRepository.save(record);

        return response;
    }

    // =========================
    // Helpers
    // =========================

    private void createEntry(Long txId, Long accountId, EntryType type, BigDecimal amount) {
        LedgerEntry entry = new LedgerEntry();
        entry.setTransactionId(txId);
        entry.setAccountId(accountId);
        entry.setEntryType(type);
        entry.setAmount(amount);
        entryRepository.save(entry);
    }

    private void validateBalanced(Long txId) {
        var entries = entryRepository.findByTransactionId(txId);

        BigDecimal debit = entries.stream()
                .filter(e -> e.getEntryType() == EntryType.DEBIT)
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal credit = entries.stream()
                .filter(e -> e.getEntryType() == EntryType.CREDIT)
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (debit.compareTo(credit) != 0) {
            throw new RuntimeException("Ledger not balanced!");
        }
    }
}