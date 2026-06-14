package com.ledger.system.service.impl;

import com.ledger.system.dto.SettlementRequest;
import com.ledger.system.dto.TransactionResponse;
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
import com.ledger.system.service.SettlementService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final AccountRepository accountRepository;
    private final LedgerTransactionRepository txRepository;
    private final IdempotencyRepository idempotencyRepository;
    private final LedgerEntryRepository entryRepository;

    private static final Long SETTLEMENT_ACCOUNT_ID = 3L;

    @Override
    @Transactional
    public TransactionResponse settle(SettlementRequest settlementRequest) {


        if (idempotencyRepository.existsByKeyValue(settlementRequest.idempotencyKey())) {
            throw new BusinessException("Duplicate Settelment Request", ErrorCode.DUPLICATE_SETTELMENT);
        }

        if(settlementRequest.amount().compareTo(BigDecimal.ZERO)<=0){
            throw new BusinessException("Invalid settlement amount",ErrorCode.INVALID_AMOUNT);
        }

        Account merchant = accountRepository.lockAccount(settlementRequest.merchantAccountId());
        Account settlement = accountRepository.lockAccount(SETTLEMENT_ACCOUNT_ID);



        LedgerTransaction tx = new LedgerTransaction();
        tx.setTransactionReference(UUID.randomUUID());
        tx.setTransactionType(TransactionType.SETTLEMENT);
        tx.setIdempotencyKey(settlementRequest.idempotencyKey());

        tx = txRepository.save(tx);
        create(tx.getId(), settlementRequest.merchantAccountId(), EntryType.CREDIT,settlementRequest.amount());
        create(tx.getId(), settlement.getId(), EntryType.DEBIT,settlementRequest.amount());

        IdempotencyKey key = new IdempotencyKey();
        key.setKeyValue(settlementRequest.idempotencyKey());
        idempotencyRepository.save(key);

        return new TransactionResponse(tx.getTransactionReference(), "SETTLEMENT_SUCCESS");

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
