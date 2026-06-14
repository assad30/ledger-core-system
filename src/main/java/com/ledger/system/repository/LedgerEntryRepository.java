package com.ledger.system.repository;

import com.ledger.system.entity.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    List<LedgerEntry> findByAccountId(Long accountId);
    List<LedgerEntry> findByTransactionId(Long transactionId);
}
