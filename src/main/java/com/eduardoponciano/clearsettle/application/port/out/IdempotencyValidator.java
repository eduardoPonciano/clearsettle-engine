package com.eduardoponciano.clearsettle.application.port.out;

import com.eduardoponciano.clearsettle.domain.model.TransactionId;

public interface IdempotencyValidator {
    boolean isAlreadyProcessed(TransactionId transactionId);
    void markAsProcessed(TransactionId transactionId);
}
