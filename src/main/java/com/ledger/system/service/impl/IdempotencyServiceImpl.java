package com.ledger.system.service.impl;

import com.ledger.system.entity.IdempotencyKey;
import com.ledger.system.repository.IdempotencyRepository;
import com.ledger.system.service.IdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyServiceImpl implements IdempotencyService {

    private final IdempotencyRepository repository;

    @Override
    public Optional<IdempotencyKey> find(String key) {
        return repository.findByKeyValue(key);
    }

    @Override
    public void save(IdempotencyKey entity) {
        repository.save(entity);
    }
}