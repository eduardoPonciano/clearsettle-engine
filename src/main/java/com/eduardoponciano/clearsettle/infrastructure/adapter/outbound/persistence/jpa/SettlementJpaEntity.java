package com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.jpa;

import com.eduardoponciano.clearsettle.domain.model.SettlementStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "settlements")
public class SettlementJpaEntity {

    @Id
    private UUID transactionId;
    
    private BigDecimal amount;
    private String currency;
    private UUID merchantId;
    private UUID customerId;
    
    @Enumerated(EnumType.STRING)
    private SettlementStatus status;
    
    private Instant createdAt;
    private Instant processedAt;

    public SettlementJpaEntity() {}

    // Getters and Setters
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public UUID getMerchantId() { return merchantId; }
    public void setMerchantId(UUID merchantId) { this.merchantId = merchantId; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public SettlementStatus getStatus() { return status; }
    public void setStatus(SettlementStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getProcessedAt() { return processedAt; }
    public void setProcessedAt(Instant processedAt) { this.processedAt = processedAt; }
}
