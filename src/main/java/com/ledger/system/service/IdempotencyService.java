package com.ledger.system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ledger.system.entity.IdempotencyKey;

import java.util.Optional;

public interface IdempotencyService {

    Optional<IdempotencyKey> find(String key);

    void save(IdempotencyKey entity);

}
