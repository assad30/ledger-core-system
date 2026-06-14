package com.ledger.system.service.impl;

import com.ledger.system.dto.TransactionResponse;
import com.ledger.system.dto.TransferRequest;
import com.ledger.system.entity.Account;
import com.ledger.system.entity.IdempotencyKey;
import com.ledger.system.entity.LedgerEntry;
import com.ledger.system.entity.LedgerTransaction;
import com.ledger.system.enums.EntryType;
import com.ledger.system.enums.ErrorCode;
import com.ledger.system.enums.TransactionType;
import com.ledger.system.exception.BusinessException;
import com.ledger.system.repository.AccountRepository;
import com.ledger.system.repository.IdempotencyRepository;
import com.ledger.system.repository.LedgerEntryRepository;
import com.ledger.system.repository.LedgerTransactionRepository;
import com.ledger.system.service.TransferService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final AccountRepository accountRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final LedgerEntryRepository entryRepository;


    @Override
    @Transactional
    public TransactionResponse processTransfer(TransferRequest transferRequest) {



        if (idempotencyRepository.existsByKeyValue(transferRequest.idempotencyKey())) {
            throw new BusinessException("Duplicate Transfer Request", ErrorCode.DUPLICATE_TRANSFER);
        }

        if(transferRequest.fromAccountId().equals(transferRequest.toAccountId())){
            throw new BusinessException("Source and destination cannot be same",ErrorCode.INVALID_TRANSFER);
        }

        validate(transferRequest.amount());

        LedgerTransaction tx = new LedgerTransaction();
        tx.setTransactionReference(UUID.randomUUID());
        tx.setTransactionType(TransactionType.TRANSFER);
        tx.setIdempotencyKey(transferRequest.idempotencyKey());

        tx = transactionRepository.save(tx);

        create(tx.getId(),transferRequest.fromAccountId(),EntryType.DEBIT,transferRequest.amount());
        create(tx.getId(),transferRequest.toAccountId(),EntryType.CREDIT,transferRequest.amount());

        IdempotencyKey key = new IdempotencyKey();
        key.setKeyValue(transferRequest.idempotencyKey());
        idempotencyRepository.save(key);


        return new TransactionResponse(tx.getTransactionReference(),"TRANSFER_SUCCESS");
    }


    private void validate(BigDecimal amount){

        if(amount.compareTo(BigDecimal.ZERO)<=0)
            throw new BusinessException("Invalid amount", ErrorCode.INVALID_AMOUNT);

    }

    private void create(Long tx,Long account,EntryType type,BigDecimal amount){

        LedgerEntry e=new LedgerEntry();
        e.setTransactionId(tx);
        e.setAccountId(account);
        e.setEntryType(type);
        e.setAmount(amount);
        entryRepository.save(e);
    }
}
