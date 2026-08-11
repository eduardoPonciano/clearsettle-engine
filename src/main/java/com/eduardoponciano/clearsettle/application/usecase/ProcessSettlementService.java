package com.eduardoponciano.clearsettle.application.usecase;

import com.eduardoponciano.clearsettle.application.port.in.ProcessSettlementUseCase;
import com.eduardoponciano.clearsettle.application.port.in.SettlementCommand;
import com.eduardoponciano.clearsettle.application.port.out.IdempotencyValidator;
import com.eduardoponciano.clearsettle.application.port.out.SettlementEventPublisher;
import com.eduardoponciano.clearsettle.application.port.out.SettlementRepository;
import com.eduardoponciano.clearsettle.domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessSettlementService implements ProcessSettlementUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessSettlementService.class);

    private final SettlementRepository repository;
    private final SettlementEventPublisher eventPublisher;
    private final IdempotencyValidator idempotencyValidator;

    public ProcessSettlementService(SettlementRepository repository,
                                    SettlementEventPublisher eventPublisher,
                                    IdempotencyValidator idempotencyValidator) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.idempotencyValidator = idempotencyValidator;
    }

    @Override
    public void execute(SettlementCommand command) {
        TransactionId txId = new TransactionId(command.transactionId());

        if (idempotencyValidator.isAlreadyProcessed(txId)) {
            log.info("Transaction {} already processed. Skipping.", txId.value());
            return;
        }

        try {
            Settlement settlement = new Settlement(
                    txId,
                    new Money(command.amount(), command.currency()),
                    new MerchantId(command.merchantId()),
                    new CustomerId(command.customerId())
            );

            repository.save(settlement);

            // Business Logic: For this showcase, we assume validation is successful
            settlement.complete();

            repository.save(settlement);
            idempotencyValidator.markAsProcessed(txId);
            
            eventPublisher.publishCompleted(settlement);
            
            log.info("Settlement for transaction {} completed successfully", txId.value());

        } catch (Exception e) {
            log.error("Failed to process settlement for transaction {}", txId.value(), e);
            // In a real scenario, we might want to reload/create and fail it
            // For now, we rely on the specific consumer logic to handle retries/DLT
            throw e; 
        }
    }
}
