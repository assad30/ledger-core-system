package com.ledger.system.repository;

import com.ledger.system.entity.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerTransactionRepository extends JpaRepository<LedgerTransaction, Long> {
}
