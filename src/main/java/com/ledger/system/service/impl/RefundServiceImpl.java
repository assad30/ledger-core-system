package com.ledger.system.service.impl;

import com.ledger.system.dto.RefundRequest;
import com.ledger.system.dto.TransactionResponse;
import com.ledger.system.entity.IdempotencyKey;
import com.ledger.system.entity.LedgerEntry;
import com.ledger.system.entity.LedgerTransaction;
import com.ledger.system.enums.EntryType;
import com.ledger.system.enums.ErrorCode;
import com.ledger.system.enums.TransactionType;
import com.ledger.system.exception.BusinessException;
import com.ledger.system.repository.IdempotencyRepository;
import com.ledger.system.repository.LedgerEntryRepository;
import com.ledger.system.repository.LedgerTransactionRepository;
import com.ledger.system.service.RefundService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefundServiceImpl implements RefundService {

    private final LedgerEntryRepository entryRepository;
    private final LedgerTransactionRepository txRepository;
    private final IdempotencyRepository idempotencyRepository;

    @Override
    @Transactional
    public TransactionResponse processRefund(RefundRequest request) {


        if (idempotencyRepository.existsByKeyValue(request.idempotencyKey())) {
            throw new BusinessException("Duplicate Refund Request", ErrorCode.DUPLICATE_REFUND);
        }

        LedgerTransaction original = txRepository.findById(
                        request.originalTransactionId())
                       .orElseThrow(() ->
                        new BusinessException("Original transaction not found",ErrorCode.TRANSACTION_NOT_FOUND));

        var entries = entryRepository.findByTransactionId(original.getId());

        BigDecimal totalRefunded = entries.stream()
                        .filter(e -> e.getEntryType() == EntryType.CREDIT)
                        .map(LedgerEntry::getAmount)
                        .reduce(BigDecimal.ZERO,BigDecimal::add);
        if (request.amount()
                .compareTo(totalRefunded) > 0) {
            throw new BusinessException(
                    "Refund amount exceeds payment",
                    ErrorCode.INVALID_REFUND);
        }

        LedgerTransaction refundTx = new LedgerTransaction();
        refundTx.setTransactionReference(UUID.randomUUID());
        refundTx.setTransactionType(TransactionType.REFUND);
        refundTx.setParentTransactionId(request.originalTransactionId());
        refundTx.setIdempotencyKey(request.idempotencyKey());

        refundTx = txRepository.save(refundTx);
        Long txId = refundTx.getId();

        IdempotencyKey key = new IdempotencyKey();
        key.setKeyValue(request.idempotencyKey());
        idempotencyRepository.save(key);


        for (LedgerEntry e : entries) {
            EntryType reverse =
                    e.getEntryType() == EntryType.DEBIT ? EntryType.CREDIT : EntryType.DEBIT;
            createEntry(refundTx.getId(), e.getAccountId(), reverse, e.getAmount());

        }
        validateBalance(refundTx.getId());
        return new TransactionResponse(refundTx.getTransactionReference(),
                "REFUND_SUCCESS");
    }


    private void validateAmount(BigDecimal amount){

        if(amount==null || amount.compareTo(BigDecimal.ZERO)<=0){
            throw new BusinessException(
                    "Amount must be greater than zero",
                    ErrorCode.INVALID_AMOUNT.INVALID_AMOUNT);
        }
    }

    private void createEntry(Long tx, Long account, EntryType type, BigDecimal amount){
        LedgerEntry entry=new LedgerEntry();

        entry.setTransactionId(tx);
        entry.setAccountId(account);
        entry.setEntryType(type);
        entry.setAmount(amount);

        entryRepository.save(entry);

    }
    private void validateBalance(Long txId){

        var entries = entryRepository.findByTransactionId(txId);
        BigDecimal debit = entries.stream()
                        .filter(e->e.getEntryType()==EntryType.DEBIT)
                        .map(LedgerEntry::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal credit = entries.stream()
                        .filter(e->e.getEntryType()==EntryType.CREDIT)
                        .map(LedgerEntry::getAmount)
                        .reduce(BigDecimal.ZERO,
                                BigDecimal::add);

        if(debit.compareTo(credit)!=0){

            throw new BusinessException(
                    "Ledger not balanced",
                    ErrorCode.LEDGER_UNBALANCED);
        }
    }
}