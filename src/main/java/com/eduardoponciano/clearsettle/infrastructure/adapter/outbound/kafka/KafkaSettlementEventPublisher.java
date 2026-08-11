package com.eduardoponciano.clearsettle.infrastructure.adapter.outbound.kafka;

import com.eduardoponciano.clearsettle.application.port.out.SettlementEventPublisher;
import com.eduardoponciano.clearsettle.domain.model.Settlement;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class KafkaSettlementEventPublisher implements SettlementEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "settlement-completed";

    public KafkaSettlementEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishCompleted(Settlement settlement) {
        Map<String, Object> event = Map.of(
                "eventId", UUID.randomUUID().toString(),
                "transactionId", settlement.getTransactionId().value().toString(),
                "status", "COMPLETED",
                "processedAt", settlement.getProcessedAt().toString()
        );
        kafkaTemplate.send(TOPIC, settlement.getTransactionId().value().toString(), event);
    }

    @Override
    public void publishFailed(Settlement settlement, String reason) {
        Map<String, Object> event = Map.of(
                "eventId", UUID.randomUUID().toString(),
                "transactionId", settlement.getTransactionId().value().toString(),
                "status", "FAILED",
                "reason", reason,
                "processedAt", Instant.now().toString()
        );
        kafkaTemplate.send(TOPIC, settlement.getTransactionId().value().toString(), event);
    }
}
