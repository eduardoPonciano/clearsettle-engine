package com.eduardoponciano.clearsettle.application.usecase;

import com.eduardoponciano.clearsettle.application.port.in.SettlementCommand;
import com.eduardoponciano.clearsettle.application.port.out.IdempotencyValidator;
import com.eduardoponciano.clearsettle.application.port.out.SettlementEventPublisher;
import com.eduardoponciano.clearsettle.application.port.out.SettlementRepository;
import com.eduardoponciano.clearsettle.domain.model.Settlement;
import com.eduardoponciano.clearsettle.domain.model.SettlementStatus;
import com.eduardoponciano.clearsettle.domain.model.TransactionId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessSettlementServiceTest {

    @Mock
    private SettlementRepository repository;
    @Mock
    private SettlementEventPublisher eventPublisher;
    @Mock
    private IdempotencyValidator idempotencyValidator;

    private ProcessSettlementService service;

    @BeforeEach
    void setUp() {
        service = new ProcessSettlementService(repository, eventPublisher, idempotencyValidator);
    }

    @Test
    @DisplayName("Should process settlement successfully when not already processed")
    void processSettlementSuccess() {
        UUID txUuid = UUID.randomUUID();
        SettlementCommand command = new SettlementCommand(
                txUuid,
                new BigDecimal("150.00"),
                "USD",
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );

        when(idempotencyValidator.isAlreadyProcessed(any(TransactionId.class))).thenReturn(false);

        service.execute(command);

        verify(repository, times(2)).save(any(Settlement.class));
        verify(idempotencyValidator).markAsProcessed(any(TransactionId.class));
        verify(eventPublisher).publishCompleted(any(Settlement.class));

        ArgumentCaptor<Settlement> settlementCaptor = ArgumentCaptor.forClass(Settlement.class);
        verify(repository, times(2)).save(settlementCaptor.capture());
        
        Settlement finalSettlement = settlementCaptor.getValue();
        assertEquals(SettlementStatus.COMPLETED, finalSettlement.getStatus());
        assertEquals(txUuid, finalSettlement.getTransactionId().value());
    }

    @Test
    @DisplayName("Should skip processing if transaction is already processed")
    void skipAlreadyProcessed() {
        SettlementCommand command = new SettlementCommand(
                UUID.randomUUID(),
                new BigDecimal("150.00"),
                "USD",
                UUID.randomUUID(),
                UUID.randomUUID(),
                Instant.now()
        );

        when(idempotencyValidator.isAlreadyProcessed(any(TransactionId.class))).thenReturn(true);

        service.execute(command);

        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishCompleted(any());
    }
}
