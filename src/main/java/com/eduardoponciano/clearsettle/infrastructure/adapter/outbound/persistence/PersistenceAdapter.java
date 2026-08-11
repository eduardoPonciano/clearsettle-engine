package com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence;

import com.eduardoponciano.clearsettle.application.port.out.IdempotencyValidator;
import com.eduardoponciano.clearsettle.application.port.out.SettlementRepository;
import com.eduardoponciano.clearsettle.domain.model.*;
import com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.jpa.SettlementJpaEntity;
import com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.jpa.SpringDataJpaSettlementRepository;
import com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.mongo.SettlementAuditEntity;
import com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.mongo.SpringDataMongoSettlementRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PersistenceAdapter implements SettlementRepository, IdempotencyValidator {

    private final SpringDataJpaSettlementRepository jpaRepository;
    private final SpringDataMongoSettlementRepository mongoRepository;

    public PersistenceAdapter(SpringDataJpaSettlementRepository jpaRepository,
                              SpringDataMongoSettlementRepository mongoRepository) {
        this.jpaRepository = jpaRepository;
        this.mongoRepository = mongoRepository;
    }

    @Override
    @Transactional
    public void save(Settlement settlement) {
        SettlementJpaEntity entity = new SettlementJpaEntity();
        entity.setTransactionId(settlement.getTransactionId().value());
        entity.setAmount(settlement.getMoney().amount());
        entity.setCurrency(settlement.getMoney().currency());
        entity.setMerchantId(settlement.getMerchantId().value());
        entity.setCustomerId(settlement.getCustomerId().value());
        entity.setStatus(settlement.getStatus());
        entity.setCreatedAt(settlement.getCreatedAt());
        entity.setProcessedAt(settlement.getProcessedAt());

        jpaRepository.save(entity);

        // Audit log in Mongo
        SettlementAuditEntity audit = new SettlementAuditEntity();
        audit.setTransactionId(settlement.getTransactionId().value());
        audit.setAmount(settlement.getMoney().amount());
        audit.setCurrency(settlement.getMoney().currency());
        audit.setStatus(settlement.getStatus());
        audit.setTimestamp(settlement.getProcessedAt() != null ? settlement.getProcessedAt() : settlement.getCreatedAt());
        audit.setDetails("Settlement state updated to " + settlement.getStatus());
        
        mongoRepository.save(audit);
    }

    @Override
    public Optional<Settlement> findByTransactionId(TransactionId transactionId) {
        return jpaRepository.findById(transactionId.value())
                .map(this::mapToDomain);
    }

    @Override
    public boolean isAlreadyProcessed(TransactionId transactionId) {
        // We use JPA for core idempotency, but could also check Mongo
        return jpaRepository.existsById(transactionId.value());
    }

    @Override
    public void markAsProcessed(TransactionId transactionId) {
        // In this implementation, save() already marks it via JPA existence.
        // markAsProcessed could be used for a separate idempotency table if needed.
    }

    private Settlement mapToDomain(SettlementJpaEntity entity) {
        // Mapping logic back to domain (simplified for showcase)
        Settlement settlement = new Settlement(
                new TransactionId(entity.getTransactionId()),
                new Money(entity.getAmount(), entity.getCurrency()),
                new MerchantId(entity.getMerchantId()),
                new CustomerId(entity.getCustomerId())
        );
        // Note: Settlement entity might need more setters if we were reloading full state
        return settlement;
    }
}
