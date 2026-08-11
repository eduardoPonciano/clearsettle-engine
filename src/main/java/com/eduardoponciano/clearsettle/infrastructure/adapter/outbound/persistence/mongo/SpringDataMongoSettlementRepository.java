package com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.UUID;

public interface SpringDataMongoSettlementRepository extends MongoRepository<SettlementAuditEntity, String> {
    boolean existsByTransactionId(UUID transactionId);
}
