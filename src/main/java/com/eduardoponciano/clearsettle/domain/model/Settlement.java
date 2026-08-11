package com.eduardoponciano.clearsettle.domain.model;

import com.eduardoponciano.clearsettle.domain.exception.DomainException;
import java.time.Instant;
import java.util.Objects;

public class Settlement {
    private final TransactionId transactionId;
    private final Money money;
    private final MerchantId merchantId;
    private final CustomerId customerId;
    private SettlementStatus status;
    private final Instant createdAt;
    private Instant processedAt;

    public Settlement(TransactionId transactionId, Money money, MerchantId merchantId, CustomerId customerId) {
        this.transactionId = Objects.requireNonNull(transactionId);
        this.money = Objects.requireNonNull(money);
        this.merchantId = Objects.requireNonNull(merchantId);
        this.customerId = Objects.requireNonNull(customerId);
        this.status = SettlementStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void complete() {
        if (this.status != SettlementStatus.PENDING) {
            throw new DomainException("Only pending settlements can be completed");
        }
        this.status = SettlementStatus.COMPLETED;
        this.processedAt = Instant.now();
    }

    public void fail(String reason) {
        if (this.status != SettlementStatus.PENDING) {
            throw new DomainException("Only pending settlements can be failed");
        }
        this.status = SettlementStatus.FAILED;
        this.processedAt = Instant.now();
    }

    // Getters
    public TransactionId getTransactionId() { return transactionId; }
    public Money getMoney() { return money; }
    public MerchantId getMerchantId() { return merchantId; }
    public CustomerId getCustomerId() { return customerId; }
    public SettlementStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getProcessedAt() { return processedAt; }
}
