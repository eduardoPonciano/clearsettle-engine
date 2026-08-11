package com.eduardoponciano.clearsettle.application.port.out;

import com.eduardoponciano.clearsettle.domain.model.Settlement;

public interface SettlementEventPublisher {
    void publishCompleted(Settlement settlement);
    void publishFailed(Settlement settlement, String reason);
}
