package com.ledger.system.service.impl;

import com.ledger.system.dto.PaymentRequest;
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
import com.ledger.system.service.LedgerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private final AccountRepository accountRepository;
    private final LedgerTransactionRepository transactionRepository;
    private final LedgerEntryRepository entryRepository;
    private final IdempotencyRepository idempotencyRepository;

    private static final Long FEES_ACCOUNT_ID = 3L;

    @Override
    @Transactional
    public TransactionResponse processPayment(PaymentRequest request) {

        if (idempotencyRepository.existsByKeyValue(request.idempotencyKey())) {
            throw new BusinessException("Duplicate Payment Request", ErrorCode.DUPLICATE_TRANSACTION);
        }
        validateAmount(request.amount(),request.fee(),request.userAccountId(),request.merchantAccountId());

        // 2. LOCK ACCOUNTS (CONCURRENCY SAFETY)
        Account user = accountRepository.lockAccount(request.userAccountId());
        Account merchant = accountRepository.lockAccount(request.merchantAccountId());

        // 3. CALCULATE AMOUNTS
        BigDecimal total = request.amount();
        BigDecimal fee = request.fee();
        BigDecimal merchantAmount = total.subtract(fee);

        // 4. CREATE TRANSACTION HEADER
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

        IdempotencyKey key = new IdempotencyKey();
        key.setKeyValue(request.idempotencyKey());
        idempotencyRepository.save(key);

        return new TransactionResponse(tx.getTransactionReference(), "SUCCESS");

    }

    private void createEntry(Long txId, Long accountId,
                             EntryType type, BigDecimal amount) {

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

    private void validateAmount(
            BigDecimal amount,
            BigDecimal fee,
            Long userAccountId,
            Long merchantAccountId) {

        validateAmount(amount);
        validateFee(amount, fee);
        validateAccounts(userAccountId, merchantAccountId);
    }

    private void validateAmount(BigDecimal amount){
        if(amount==null || amount.compareTo(BigDecimal.ZERO)<=0){
            throw new BusinessException("Amount must be greater than zero",
                    ErrorCode.INVALID_AMOUNT);
        }
    }

    private void validateFee(BigDecimal amount,BigDecimal fee){

        if (fee == null || amount.compareTo(fee) <= 0) {
            throw new BusinessException("Amount must be greater than Fee",
                    ErrorCode.INVALID_FEE);
        }
    }

    private void validateAccounts(Long userAccountId,Long merchantAccountId){

        if (userAccountId == null || merchantAccountId == null || userAccountId.equals(merchantAccountId)) {
            throw new BusinessException("User Account can not be as merchant Account",
                    ErrorCode.ACCOUNT_INVALID);
        }
    }
}
