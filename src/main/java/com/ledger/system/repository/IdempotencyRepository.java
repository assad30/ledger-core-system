package com.ledger.system.repository;

import com.ledger.system.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRepository extends JpaRepository<IdempotencyKey, Long> {

    boolean existsByKeyValue(String keyValue);
    Optional<IdempotencyKey> findByKeyValue(String keyValue);
}
