package com.eduardoponciano.clearsettle.infrastructure.adapter.inbound.kafka;

import com.eduardoponciano.clearsettle.application.port.in.ProcessSettlementUseCase;
import com.eduardoponciano.clearsettle.application.port.in.SettlementCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class SettlementKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(SettlementKafkaConsumer.class);
    private final ProcessSettlementUseCase useCase;
    private final ObjectMapper objectMapper;

    public SettlementKafkaConsumer(ProcessSettlementUseCase useCase, ObjectMapper objectMapper) {
        this.useCase = useCase;
        this.objectMapper = objectMapper;
    }

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            topicSuffixingStrategy = org.springframework.kafka.retrytopic.TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "settlement-requested", groupId = "settlement-group")
    public void consume(@Payload Map<String, Object> payload) {
        log.info("Received settlement request event: {}", payload);
        
        try {
            SettlementCommand command = new SettlementCommand(
                    UUID.fromString((String) payload.get("transactionId")),
                    new BigDecimal(payload.get("amount").toString()),
                    (String) payload.get("currency"),
                    UUID.fromString((String) payload.get("merchantId")),
                    UUID.fromString((String) payload.get("customerId")),
                    Instant.parse((String) payload.get("occurredAt"))
            );

            useCase.execute(command);
            
        } catch (Exception e) {
            log.error("Error processing settlement event", e);
            throw e; // Trigger retry/DLT
        }
    }

    @DltHandler
    public void handleDlt(Map<String, Object> payload) {
        log.error("Event moved to DLT: {}", payload);
        // Here we could persist to a specialized table or notify ops
    }
}
