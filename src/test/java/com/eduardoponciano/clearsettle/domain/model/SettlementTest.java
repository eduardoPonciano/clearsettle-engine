package com.eduardoponciano.clearsettle.domain.model;

import com.eduardoponciano.clearsettle.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SettlementTest {

    @Test
    @DisplayName("Should create a pending settlement correctly")
    void createSettlement() {
        TransactionId txId = TransactionId.generate();
        Money money = new Money(new BigDecimal("100.00"), "USD");
        MerchantId merchantId = new MerchantId(UUID.randomUUID());
        CustomerId customerId = new CustomerId(UUID.randomUUID());

        Settlement settlement = new Settlement(txId, money, merchantId, customerId);

        assertEquals(txId, settlement.getTransactionId());
        assertEquals(money, settlement.getMoney());
        assertEquals(SettlementStatus.PENDING, settlement.getStatus());
        assertNotNull(settlement.getCreatedAt());
        assertNull(settlement.getProcessedAt());
    }

    @Test
    @DisplayName("Should complete a pending settlement")
    void completeSettlement() {
        Settlement settlement = createSampleSettlement();
        settlement.complete();

        assertEquals(SettlementStatus.COMPLETED, settlement.getStatus());
        assertNotNull(settlement.getProcessedAt());
    }

    @Test
    @DisplayName("Should throw exception when completing a non-pending settlement")
    void completeInvalidSettlement() {
        Settlement settlement = createSampleSettlement();
        settlement.complete();

        assertThrows(DomainException.class, settlement::complete);
    }

    @Test
    @DisplayName("Should fail a pending settlement")
    void failSettlement() {
        Settlement settlement = createSampleSettlement();
        settlement.fail("Insufficient funds");

        assertEquals(SettlementStatus.FAILED, settlement.getStatus());
        assertNotNull(settlement.getProcessedAt());
    }

    private Settlement createSampleSettlement() {
        return new Settlement(
                TransactionId.generate(),
                new Money(new BigDecimal("100.00"), "USD"),
                new MerchantId(UUID.randomUUID()),
                new CustomerId(UUID.randomUUID())
        );
    }
}
