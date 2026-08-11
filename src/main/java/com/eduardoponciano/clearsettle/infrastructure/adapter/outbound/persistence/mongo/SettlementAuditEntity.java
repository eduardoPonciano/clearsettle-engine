package com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.persistence.mongo;

import com.eduardoponciano.clearsettle.domain.model.SettlementStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "settlement_audit")
public class SettlementAuditEntity {
    @Id
    private String id;
    private UUID transactionId;
    private BigDecimal amount;
    private String currency;
    private SettlementStatus status;
    private Instant timestamp;
    private String details;

    public SettlementAuditEntity() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public SettlementStatus getStatus() { return status; }
    public void setStatus(SettlementStatus status) { this.status = status; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
