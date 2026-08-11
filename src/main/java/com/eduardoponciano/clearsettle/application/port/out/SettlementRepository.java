package com.eduardoponciano.clearsettle.application.port.out;

import com.eduardoponciano.clearsettle.domain.model.Settlement;
import com.eduardoponciano.clearsettle.domain.model.TransactionId;
import java.util.Optional;

public interface SettlementRepository {
    void save(Settlement settlement);
    Optional<Settlement> findByTransactionId(TransactionId transactionId);
}
