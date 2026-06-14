package com.ledger.system.controller;

import com.ledger.system.entity.LedgerEntry;
import com.ledger.system.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountLedgerController {

    private final LedgerEntryRepository ledgerEntryRepository;

    // ================= LEDGER VIEW =================
    @GetMapping("/{id}/ledger")
    public List<LedgerEntry> getLedger(@PathVariable Long id) {

        return ledgerEntryRepository.findByAccountId(id);
    }
}
